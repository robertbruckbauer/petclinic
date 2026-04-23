import express from "express";
import { randomUUID } from "node:crypto";
import { Kafka } from "kafkajs";
import { DatabaseSync } from "node:sqlite";
import { mkdirSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import logger from "./logger.js";

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

function createDirectory(id) {
  const buildDir = join(__dirname, "..", "..", "..", "build");
  try {
    mkdirSync(buildDir, { recursive: true });
    logger.info("Directory", `buildDir: ${buildDir}`);
    return buildDir;
  } catch (mkdirError) {
    logger.error("Directory", `Error creating build directory:`, mkdirError);
    throw mkdirError;
  }
}

/**
 * Initialize SQLite database with schema
 */
function createDatabase(id, buildDir) {
  const sqlitePath = join(buildDir, `${id}.sqlite`);
  logger.info("Database", `Initializing database at ${sqlitePath}`);
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

  logger.info("Database", "Database schema created");
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
      "table.include.list":
        "public.enum,public.ping,public.owner,public.pet,public.vet,public.visit",
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
  logger.info("Connector", "Creating Debezium connector...");

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

    logger.info("Connector", "Debezium connector created");
  } catch (error) {
    logger.error("Connector", "Error creating connector:", error);
    throw error;
  }
}

/**
 * Delete Debezium connector
 */
async function deleteConnector(id) {
  const connectorName = buildConnectorName(id);
  logger.info("Connector", "Deleting Debezium connector...");

  try {
    const response = await fetch(`${CONNECT_URL}/connectors/${connectorName}`, {
      method: "DELETE",
    });

    if (response.ok || response.status === 404) {
      logger.info("Connector", "Connector deleted successfully");
    } else {
      logger.warn(
        "Connector",
        `Failed to delete connector: ${response.status}`
      );
    }
  } catch (error) {
    logger.error("Connector", "Error deleting connector:", error);
  }
}

/**
 * Wait for Debezium connector to be ready
 */
async function waitForConnector(id, maxWaitMs = 30000, pollIntervalMs = 500) {
  const connectorName = buildConnectorName(id);
  logger.info("Connector", "Waiting for connector to be ready...");

  const startTime = Date.now();
  while (Date.now() - startTime < maxWaitMs) {
    try {
      const response = await fetch(
        `${CONNECT_URL}/connectors/${connectorName}/status`
      );

      if (response.ok) {
        const status = await response.json();
        if (
          status.connector?.state === "RUNNING" &&
          status.tasks?.[0]?.state === "RUNNING"
        ) {
          logger.info("Connector", "Connector is ready");
          return true;
        }
        logger.info(
          "Connector",
          `Status: connector=${status.connector?.state}, task=${status.tasks?.[0]?.state}`
        );
      }
    } catch (error) {
      logger.debug("Connector", "Polling connector status...");
    }

    await new Promise((resolve) => setTimeout(resolve, pollIntervalMs));
  }

  throw new Error("Connector did not become ready in time");
}

/**
 * Send ping kill message to stop consumer
 */
async function sendPingKillMessage(id) {
  logger.info("Consumer", `Sending kill message with id: ${id}`);

  try {
    const response = await fetch(`${BACKEND_URL}/api/ping/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(
        `Failed to send ping kill message: ${response.status} ${errorText}`
      );
    }

    logger.info("Consumer", "Kill message sent successfully");
  } catch (error) {
    logger.error("Consumer", "Error sending kill message:", error);
    throw error;
  }
}

/**
 * Create topic using Admin API
 */
async function createTopic(id, topicName) {
  logger.info("Consumer", `Creating ${topicName} topic...`);

  const kafka = new Kafka({
    clientId: `tester-admin-${id}`,
    brokers: KAFKA_BROKERS.split(","),
  });

  const admin = kafka.admin();

  try {
    await admin.connect();
    logger.info("Consumer", "Admin connected");

    await admin.createTopics({
      topics: [
        {
          topic: topicName,
          numPartitions: 1,
          replicationFactor: 1,
        },
      ],
    });

    logger.info("Consumer", `Topic ${topicName} created`);
  } catch (error) {
    // Ignore if topic already exists
    if (error.message && error.message.includes("already exists")) {
      logger.info("Consumer", `Topic ${topicName} already exists`);
    } else {
      logger.error("Consumer", "Error creating topic:", error);
      throw error;
    }
  } finally {
    await admin.disconnect();
    logger.info("Consumer", "Admin disconnected");
  }
}

/**
 * Start Kafka consumer
 */
async function startConsumer(id, database, topicName) {
  logger.info("Consumer", "Starting consumer...");

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
        logger.debug(
          "Consumer",
          `Received message from topic: ${topic}, partition: ${partition}`
        );

        // Check for X-RUN-ID header
        const headers = message.headers || {};
        const messageRunId = headers["X-RUN-ID"]?.toString();

        logger.debug(
          "Consumer",
          `Message RUN-ID: ${messageRunId}, Expected: ${id}`
        );

        if (messageRunId !== id) {
          logger.debug("Consumer", `Skipping message - RUN-ID mismatch`);
          return; // Skip messages not for this run
        }

        // Handle null key (shouldn't happen with Debezium, but be safe)
        if (!message.key) {
          logger.debug("Consumer", `Skipping message with null key`);
          return;
        }

        // Handle null value (tombstone message for compacted topics)
        if (!message.value) {
          logger.debug("Consumer", `Skipping tombstone message`);
          return;
        }

        const key = JSON.parse(message.key.toString());
        const value = JSON.parse(message.value.toString());

        logger.debug("Consumer", `Message key:`, key);
        logger.debug("Consumer", `Message value:`, value);

        // Extract fields - Debezium format without schema wrapper
        const keyId = key.id;
        const operation = value.op;
        const tableName = value.source?.table;
        const beforeData = value.before ? JSON.stringify(value.before) : null;
        const afterData = value.after ? JSON.stringify(value.after) : null;

        if (!keyId || !operation || !tableName) {
          logger.warn("Consumer", `Invalid message structure, skipping`);
          return;
        }

        logger.info(
          "Consumer",
          `Processing CDC event: entity_id=${keyId}, entity_type=${tableName}, operation=${operation}`
        );

        // Check if this is a ping kill message
        if (tableName === "ping" && keyId === id) {
          logger.info(
            "Consumer",
            `Received ping kill message for RUN-ID: ${id}`
          );

          // Mark consumer as shutting down immediately
          consumerRunning = false;
        }

        if (!consumerRunning) {
          // Wait a bit to allow any in-flight CDC events to be processed
          logger.info("Consumer", "Waiting 2 seconds for pending messages...");
          setTimeout(async () => {
            await consumer.stop();
            logger.info("Consumer", "Consumer stopped by kill message");
          }, 2000);

          // Don't store ping events in CDC table
          return;
        }

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

        logger.info(
          "Consumer",
          `Stored CDC event: ${tableName}.${keyId} (${operation})`
        );
      } catch (error) {
        logger.error("Consumer", "Error processing message:", error);
        logger.error("Consumer", "Message:", message);
      }
    },
  });

  logger.info("Consumer", "Consumer started");
}

/**
 * Stop Kafka consumer
 */
async function stopConsumer() {
  if (consumer && consumerRunning) {
    logger.info("Consumer", "Stopping consumer...");
    consumerRunning = false;
    await consumer.stop();
    await consumer.disconnect();
    consumer = null;
    logger.info("Consumer", "Consumer stopped");
  }
  kafka = null;
}

/**
 * Perform setup operations
 * @returns {Promise<string>} - The generated runId
 */
async function performSetup() {
  // Generate unique RUN-ID
  runId = randomUUID();
  logger.info("Setup", `Generated RUN-ID: ${runId}`);

  // Ensure build directory exists
  const buildDir = createDirectory(runId);

  // Initialize database
  sqlite = createDatabase(runId, buildDir);

  // Define topic name
  const topicName = "petclinic.all";

  // Create topic
  await createTopic(runId, topicName);

  // Create Debezium connector
  await createConnector(runId, topicName);

  // Wait for connector to be ready
  await waitForConnector(runId);

  // Start Kafka consumer
  await startConsumer(runId, sqlite, topicName);

  logger.info("Setup", "Setup complete");
  return runId;
}

/**
 * Perform teardown cleanup operations
 * @param {string} scope - Logging scope (e.g., "Setup", "Teardown")
 * @param {boolean} graceful - Whether to handle errors gracefully
 * @returns {Promise<string>} - The runId that was torn down
 */
async function performTeardown(scope, graceful = true) {
  const currentRunId = runId;
  if (!currentRunId) {
    logger.info(scope, "No active session to tear down");
    return null;
  }

  logger.info(scope, `Starting teardown for RUN-ID: ${currentRunId}`);

  // Send ping kill message
  try {
    await sendPingKillMessage(currentRunId);
  } catch (pingError) {
    const message = `Error sending ping kill message${graceful ? " (continuing anyway)" : ""}:`;
    if (graceful) {
      logger.warn(scope, message, pingError);
    } else {
      logger.error(scope, message, pingError);
      throw pingError;
    }
  }

  // Wait for consumer to stop (max 5 seconds)
  const maxWait = 5000;
  const pollInterval = 100;
  const startTime = Date.now();
  while (consumerRunning && Date.now() - startTime < maxWait) {
    await new Promise((resolve) => setTimeout(resolve, pollInterval));
  }

  if (consumerRunning) {
    logger.warn(scope, "Consumer did not stop in time, forcing stop");
    try {
      await stopConsumer();
    } catch (stopError) {
      const message = `Error stopping consumer${graceful ? " (continuing anyway)" : ""}:`;
      if (graceful) {
        logger.warn(scope, message, stopError);
      } else {
        logger.error(scope, message, stopError);
        throw stopError;
      }
    }
  } else {
    logger.info(scope, "Consumer stopped successfully");
    // Wait a bit more for the setTimeout to complete the disconnect
    await new Promise((resolve) => setTimeout(resolve, 2500));

    // Disconnect consumer
    if (consumer) {
      try {
        await consumer.disconnect();
        consumer = null;
        kafka = null;
      } catch (disconnectError) {
        const message = `Error disconnecting consumer${graceful ? " (continuing anyway)" : ""}:`;
        if (graceful) {
          logger.warn(scope, message, disconnectError);
          consumer = null;
          kafka = null;
        } else {
          logger.error(scope, message, disconnectError);
          throw disconnectError;
        }
      }
    }
  }

  // Close database
  if (sqlite) {
    try {
      sqlite.close();
      logger.info(scope, "Database closed");
      sqlite = null;
    } catch (dbError) {
      const message = `Error closing database${graceful ? " (continuing anyway)" : ""}:`;
      if (graceful) {
        logger.warn(scope, message, dbError);
        sqlite = null;
      } else {
        logger.error(scope, message, dbError);
        throw dbError;
      }
    }
  }

  // Delete Debezium connector
  try {
    await deleteConnector(currentRunId);
  } catch (connectorError) {
    const message = `Error deleting connector${graceful ? " (continuing anyway)" : ""}:`;
    if (graceful) {
      logger.warn(scope, message, connectorError);
    } else {
      logger.error(scope, message, connectorError);
      throw connectorError;
    }
  }

  runId = null;
  logger.info(scope, `Teardown complete for RUN-ID: ${currentRunId}`);
  return currentRunId;
}

/**
 * Endpoint: POST /setup
 * @returns {Promise<SetupResponse | ErrorResponse>}
 */
app.post("/setup", async (req, res) => {
  try {
    // If setup already called, automatically teardown first
    if (runId) {
      logger.warn(
        "Setup",
        `Setup already called with RUN-ID: ${runId}. Performing automatic teardown first.`
      );

      try {
        await performTeardown("Setup", false);
      } catch (teardownError) {
        logger.error(
          "Setup",
          "Error during automatic teardown:",
          teardownError
        );
      } finally {
        // Reset state anyway to allow setup to proceed
        runId = null;
        sqlite = null;
        consumer = null;
        kafka = null;
        consumerRunning = false;
      }
    }

    const newRunId = await performSetup();
    res.json({ runId: newRunId, status: "success" });
  } catch (error) {
    logger.error("Setup", "Error:", error);
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

    logger.info(
      "Log",
      `Logged use case: ${testName} - ${entityType}#${entityId}`
    );
    res.json({ status: "success" });
  } catch (error) {
    logger.error("Log", "Error:", error);
    res.status(500).json({ error: error.message });
  }
});

/**
 * Endpoint: POST /teardown
 * @returns {Promise<TeardownResponse | ErrorResponse>}
 */
app.post("/teardown", async (req, res) => {
  try {
    const completedRunId = await performTeardown("Teardown", true);

    if (completedRunId === null) {
      return res.status(200).json({
        runId: null,
        status: "success",
        message: "No active session to tear down",
      });
    }

    res.json({ runId: completedRunId, status: "success" });
  } catch (error) {
    logger.error("Teardown", "Error:", error);
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
  logger.info("Server", `Tester service listening on port ${PORT}`);
});
