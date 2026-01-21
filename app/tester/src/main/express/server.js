import express from "express";
import { randomUUID } from "node:crypto";
import { Kafka } from "kafkajs";
import { DatabaseSync } from "node:sqlite";
import { mkdir } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

/**
 * @typedef {import('../types/setup.js').SetupResponse} SetupResponse
 * @typedef {import('../types/teardown.js').TeardownResponse} TeardownResponse
 * @typedef {import('../types/log.js').LogResponse} LogResponse
 * @typedef {import('../types/error.js').ErrorResponse} ErrorResponse
 * @typedef {import('../types/log.js').LogRequest} LogRequest
 */

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const app = express();
app.use(express.json());

// Environment variables
const BACKEND_URL = process.env.BACKEND_URL || "http://server:8080";
const CONNECT_URL = process.env.CONNECT_URL || "http://debezium:8083";
const POSTGRES_HOST = process.env.POSTGRES_HOST || "postgres17";
const POSTGRES_PORT = process.env.POSTGRES_PORT || "5432";
const POSTGRES_USER = process.env.POSTGRES_USER || "sa";
const POSTGRES_PASSWORD = process.env.POSTGRES_PASSWORD || "P@ssw0rd";
const POSTGRES_DB = process.env.POSTGRES_DB || "postgres";
const KAFKA_BROKERS = process.env.KAFKA_BROKERS || "redpanda:9643";

// Global state
let runId = null;
let sqlite = null;
let kafka = null;
let consumer = null;
let consumerRunning = false;

/**
 * Initialize SQLite database with schema
 */
function createDatabase(id) {
  const buildDir = join(__dirname, "..", "..", "..", "build");
  const sqlitePath = join(buildDir, `${id}.sqlite`);

  console.log(`[DB] Initializing database at ${sqlitePath}`);
  console.log(`[DB] __dirname: ${__dirname}`);
  console.log(`[DB] buildDir: ${buildDir}`);

  const database = new DatabaseSync(sqlitePath);

  // Create CDC events table
  database.exec(`
    CREATE TABLE IF NOT EXISTS petclinic_cdc (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      timestamp TEXT NOT NULL,
      entity_id TEXT NOT NULL,
      entity_type TEXT NOT NULL,
      operation TEXT NOT NULL,
      before_data TEXT,
      after_data TEXT
    )
  `);

  // Create use case log table
  database.exec(`
    CREATE TABLE IF NOT EXISTS petclinic_log (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      timestamp TEXT NOT NULL,
      test_name TEXT NOT NULL,
      entity_type TEXT NOT NULL,
      entity_id TEXT NOT NULL
    )
  `);

  console.log("[DB] Database schema created");
  return database;
}

/**
 * Build Debezium connector name
 */
function buildConnectorName(id) {
  return `petclinic-connector-${id}`;
}

/**
 * Build Debezium connector configuration
 */
function buildConnectorConfig(id, topicName) {
  return {
    name: `${buildConnectorName(id)}`,
    config: {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": POSTGRES_HOST,
      "database.port": POSTGRES_PORT,
      "database.user": POSTGRES_USER,
      "database.password": POSTGRES_PASSWORD,
      "database.dbname": POSTGRES_DB,
      "database.server.name": "petclinic",
      "topic.prefix": "petclinic",
      "snapshot.mode": "never",
      "table.include.list": "public.owner,public.pet,public.vet,public.visit",
      "slot.name": `petclinic_slot_${id.replace(/-/g, "_")}`,
      "plugin.name": "pgoutput",
      "publication.name": `petclinic_pub_${id.replace(/-/g, "_")}`,
      transforms: "route,addHeader",
      "transforms.route.type":
        "org.apache.kafka.connect.transforms.RegexRouter",
      "transforms.route.regex": "petclinic\\.public\\.(.*)",
      "transforms.route.replacement": topicName,
      "transforms.addHeader.type":
        "org.apache.kafka.connect.transforms.InsertHeader",
      "transforms.addHeader.header": "X-RUN-ID",
      "transforms.addHeader.value.literal": id,
      "key.converter": "org.apache.kafka.connect.json.JsonConverter",
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",
      "key.converter.schemas.enable": "false",
      "value.converter.schemas.enable": "false",
    },
  };
}

/**
 * Create Debezium connector
 */
async function createConnector(id, topicName) {
  const connectorConfig = buildConnectorConfig(id, topicName);
  console.log("[Connector] Creating Debezium connector...");

  try {
    const createResponse = await fetch(`${CONNECT_URL}/connectors`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(connectorConfig),
    });

    if (!createResponse.ok) {
      const errorText = await createResponse.text();
      throw new Error(
        `Failed to create connector: ${createResponse.status} ${errorText}`
      );
    }

    console.log("[Connector] Debezium connector created");
  } catch (error) {
    console.error("[Connector] Error creating connector:", error);
    throw error;
  }
}

/**
 * Delete Debezium connector
 */
async function deleteConnector(id) {
  const connectorName = buildConnectorName(id);
  console.log("[Connector] Deleting Debezium connector...");

  try {
    const response = await fetch(`${CONNECT_URL}/connectors/${connectorName}`, {
      method: "DELETE",
    });

    if (response.ok || response.status === 404) {
      console.log("[Connector] Connector deleted successfully");
    } else {
      console.warn(
        `[Connector] Failed to delete connector: ${response.status}`
      );
    }
  } catch (error) {
    console.error("[Connector] Error deleting connector:", error);
  }
}

/**
 * Create topic using Admin API
 */
async function createTopic(id, topicName) {
  console.log(`[Kafka] Creating ${topicName} topic...`);

  const kafka = new Kafka({
    clientId: `tester-admin-${id}`,
    brokers: KAFKA_BROKERS.split(","),
  });

  const admin = kafka.admin();

  try {
    await admin.connect();
    console.log("[Kafka] Admin connected");

    await admin.createTopics({
      topics: [
        {
          topic: topicName,
          numPartitions: 1,
          replicationFactor: 1,
        },
      ],
    });

    console.log(`[Kafka] Topic ${topicName} created`);
  } catch (error) {
    // Ignore if topic already exists
    if (error.message && error.message.includes("already exists")) {
      console.log(`[Kafka] Topic ${topicName} already exists`);
    } else {
      console.error("[Kafka] Error creating topic:", error);
      throw error;
    }
  } finally {
    await admin.disconnect();
    console.log("[Kafka] Admin disconnected");
  }
}

/**
 * Start Kafka consumer
 */
async function startConsumer(id, database, topicName) {
  console.log("[Kafka] Starting consumer...");

  kafka = new Kafka({
    clientId: `tester-${id}`,
    brokers: KAFKA_BROKERS.split(","),
  });

  consumer = kafka.consumer({ groupId: `tester-group-${id}` });

  await consumer.connect();
  await consumer.subscribe({
    topic: topicName,
    fromBeginning: true,
  });

  consumerRunning = true;

  consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      try {
        console.log(
          `[Kafka] Received message from topic: ${topic}, partition: ${partition}`
        );

        // Check for X-RUN-ID header
        const headers = message.headers || {};
        const messageRunId = headers["X-RUN-ID"]?.toString();

        console.log(`[Kafka] Message RUN-ID: ${messageRunId}, Expected: ${id}`);

        if (messageRunId !== id) {
          console.log(`[Kafka] Skipping message - RUN-ID mismatch`);
          return; // Skip messages not for this run
        }

        const key = JSON.parse(message.key.toString());
        const value = JSON.parse(message.value.toString());

        console.log(`[Kafka] Message key:`, key);
        console.log(`[Kafka] Message value payload:`, value.payload);

        const keyId = key.id;
        const operation = value.payload.op;
        const tableName = value.payload.source.table;
        const beforeData = value.payload.before
          ? JSON.stringify(value.payload.before)
          : null;
        const afterData = value.payload.after
          ? JSON.stringify(value.payload.after)
          : null;

        console.log(
          `[Kafka] Storing CDC event: entity_id=${keyId}, entity_type=${tableName}, operation=${operation}`
        );

        // Store in database
        const stmt = database.prepare(`
          INSERT INTO petclinic_cdc (timestamp, entity_id, entity_type, operation, before_data, after_data)
          VALUES (?, ?, ?, ?, ?, ?)
        `);

        stmt.run(
          new Date().toISOString(),
          keyId,
          tableName,
          operation,
          beforeData,
          afterData
        );

        console.log(
          `[Kafka] Stored CDC event: ${tableName}.${keyId} (${operation})`
        );
      } catch (error) {
        console.error("[Kafka] Error processing message:", error);
        console.error("[Kafka] Message:", message);
      }
    },
  });

  console.log("[Kafka] Consumer started");
}

/**
 * Stop Kafka consumer
 */
async function stopConsumer() {
  if (consumer && consumerRunning) {
    console.log("[Kafka] Stopping consumer...");
    consumerRunning = false;
    await consumer.stop();
    await consumer.disconnect();
    consumer = null;
    console.log("[Kafka] Consumer stopped");
  }
  kafka = null;
}

/**
 * Endpoint: POST /setup
 * @returns {Promise<SetupResponse | ErrorResponse>}
 */
app.post("/setup", async (req, res) => {
  try {
    if (runId) {
      return res
        .status(400)
        .json({ error: "Setup already called. Call /teardown first." });
    }

    // Generate unique RUN-ID
    runId = randomUUID();
    console.log(`[Setup] Generated RUN-ID: ${runId}`);

    // Ensure build directory exists
    const buildDir = join(__dirname, "..", "..", "..", "build");
    console.log(`[Setup] Creating build directory: ${buildDir}`);
    try {
      await mkdir(buildDir, { recursive: true });
    } catch (mkdirError) {
      console.error(`[Setup] Error creating build directory:`, mkdirError);
      throw mkdirError;
    }

    // Initialize database
    sqlite = createDatabase(runId);

    // Define topic name
    const topicName = "petclinic.all";

    // Create topic
    await createTopic(runId, topicName);

    // Create Debezium connector
    await createConnector(runId, topicName);

    // Start Kafka consumer
    await startConsumer(runId, sqlite, topicName);

    console.log("[Setup] Setup complete");
    res.json({ runId, status: "success" });
  } catch (error) {
    console.error("[Setup] Error:", error);
    res.status(500).json({ error: error.message });
  }
});

/**
 * Endpoint: POST /log
 * @param {LogRequest} req.body
 * @returns {Promise<LogResponse | ErrorResponse>}
 */
app.post("/log", async (req, res) => {
  try {
    if (!runId || !sqlite) {
      return res
        .status(400)
        .json({ error: "Setup not called yet. Call /setup first." });
    }

    const { testName, entityType, entityId } = req.body;

    if (!testName || !entityType || !entityId) {
      return res.status(400).json({
        error: "Missing required fields: testName, entityType, entityId",
      });
    }

    const stmt = sqlite.prepare(`
      INSERT INTO petclinic_log (timestamp, test_name, entity_type, entity_id)
      VALUES (?, ?, ?, ?)
    `);

    stmt.run(new Date().toISOString(), testName, entityType, entityId);

    console.log(
      `[Log] Logged use case: ${testName} - ${entityType}#${entityId}`
    );
    res.json({ status: "success" });
  } catch (error) {
    console.error("[Log] Error:", error);
    res.status(500).json({ error: error.message });
  }
});

/**
 * Endpoint: POST /teardown
 * @returns {Promise<TeardownResponse | ErrorResponse>}
 */
app.post("/teardown", async (req, res) => {
  try {
    if (!runId) {
      return res
        .status(400)
        .json({ error: "Setup not called yet. Nothing to tear down." });
    }

    console.log(`[Teardown] Starting teardown for RUN-ID: ${runId}`);

    // Stop Kafka consumer
    await stopConsumer();

    // Close database
    if (sqlite) {
      sqlite.close();
      console.log("[Teardown] Database closed");
      sqlite = null;
    }

    // Delete Debezium connector
    await deleteConnector(runId);

    const completedRunId = runId;
    runId = null;

    console.log("[Teardown] Teardown complete");
    res.json({ runId: completedRunId, status: "success" });
  } catch (error) {
    console.error("[Teardown] Error:", error);
    res.status(500).json({ error: error.message });
  }
});

// Health check endpoint
app.get("/health", (req, res) => {
  res.json({
    status: "ok",
    runId: runId || null,
    consumerRunning,
  });
});

const PORT = process.env.PORT || 9090;
app.listen(PORT, () => {
  console.log(`[Server] Tester service listening on port ${PORT}`);
});
