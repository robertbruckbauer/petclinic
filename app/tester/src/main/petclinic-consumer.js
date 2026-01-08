import { Kafka } from "kafkajs";
import Database from "better-sqlite3";
import { fileURLToPath } from "url";
import { dirname, join } from "path";
import { mkdirSync, existsSync, unlinkSync } from "fs";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const KAFKA_BROKERS = ["localhost:9092"];
const TOPIC = "petclinic.all";
const GROUP_ID = "petclinic-consumer";

/**
 * Logs messages from SQLite database for a given RUN-ID
 */
async function logMessages(runId) {
  if (!runId) {
    console.error("Error: RUN-ID is required");
    console.log("Usage: node petclinic-consumer.js log <run-id>");
    process.exit(1);
  }

  const buildDir = join(__dirname, "..", "..", "build");
  const dbPath = join(buildDir, `${runId}.db`);

  if (!existsSync(dbPath)) {
    console.error(`Error: Database file does not exist: ${dbPath}`);
    console.error(`Please run 'store' command first to create the database.`);
    process.exit(1);
  }

  console.log(`Reading from database: ${dbPath}\n`);

  try {
    const db = new Database(dbPath, { readonly: true });

    const messages = db.prepare("SELECT * FROM messages").all();

    if (messages.length === 0) {
      console.log("No messages found in database.\n");
    } else {
      messages.forEach((msg, index) => {
        console.log("═".repeat(80));
        console.log(`Message ${index + 1} of ${messages.length}`);
        console.log("─".repeat(80));
        console.log("ID:", msg.id);
        console.log("Table:", msg.table);
        console.log("Operation:", msg.op);
        console.log("─".repeat(80));
        console.log("Before:");
        console.log(
          msg.before
            ? JSON.stringify(JSON.parse(msg.before), null, 2)
            : "(null)"
        );
        console.log("─".repeat(80));
        console.log("After:");
        console.log(
          msg.after ? JSON.stringify(JSON.parse(msg.after), null, 2) : "(null)"
        );
        console.log("═".repeat(80));
        console.log("");
      });

      console.log(`Total: ${messages.length} message(s)\n`);
    }

    db.close();
    process.exit(0);
  } catch (error) {
    console.error("Error reading database:", error.message);
    process.exit(1);
  }
}

/**
 * Lists all distinct X-RUN-ID values from messages in petclinic.all topic
 */
async function listRunIds() {
  console.log(`Scanning topic: ${TOPIC} for distinct X-RUN-ID values\n`);

  const kafka = new Kafka({
    clientId: "petclinic-runid-scanner",
    brokers: KAFKA_BROKERS,
  });

  const consumer = kafka.consumer({
    groupId: `${GROUP_ID}-scanner-${Date.now()}`,
  });

  const messages = [];

  try {
    await consumer.connect();
    console.log("Connected to Kafka");

    await consumer.subscribe({
      topic: TOPIC,
      fromBeginning: true,
    });
    console.log(`Subscribed to topic: ${TOPIC}`);
    console.log("Reading messages...\n");

    // Track if we've reached the end
    let messageCount = 0;
    let lastMessageTime = Date.now();
    const TIMEOUT_MS = 3000; // Wait 3 seconds after last message

    await consumer.run({
      eachMessage: async ({ message }) => {
        messageCount++;
        lastMessageTime = Date.now();

        // Extract X-RUN-ID from headers
        const runId =
          message.headers && message.headers["X-RUN-ID"]
            ? message.headers["X-RUN-ID"].toString()
            : null;

        // Extract key
        const key = message.key ? JSON.parse(message.key.toString()) : null;
        const id = key?.id || null;

        // Extract payload
        const payload = message.value
          ? JSON.parse(message.value.toString())
          : null;
        const sourceTable = payload?.source?.table || null;
        const op = payload?.op || null;

        messages.push({
          runId,
          id,
          sourceTable,
          op,
        });
      },
    });

    // Wait for messages to finish
    while (Date.now() - lastMessageTime < TIMEOUT_MS) {
      await new Promise((resolve) => setTimeout(resolve, 500));
    }

    await consumer.disconnect();

    console.log(`Processed ${messageCount} messages\n`);
    console.log("═".repeat(80));
    console.log("Messages:");
    console.log("═".repeat(80));

    if (messages.length === 0) {
      console.log("(no messages found)");
    } else {
      messages.forEach((msg, index) => {
        console.log(`${index + 1}.`);
        console.log(`   X-RUN-ID:     ${msg.runId || "(null)"}`);
        console.log(`   id:           ${msg.id || "(null)"}`);
        console.log(`   source.table: ${msg.sourceTable || "(null)"}`);
        console.log(`   op:           ${msg.op || "(null)"}`);
        console.log("");
      });
    }

    console.log("═".repeat(80));
    console.log(`\nTotal: ${messages.length} message(s)\n`);

    process.exit(0);
  } catch (error) {
    console.error("Error listing messages:", error.message);
    await consumer.disconnect();
    process.exit(1);
  }
}

/**
 * Stores messages from petclinic.all topic to SQLite database
 */
async function storeMessages(runId) {
  if (!runId) {
    console.error("Error: RUN-ID is required");
    console.log("Usage: node petclinic-consumer.js store <run-id>");
    process.exit(1);
  }

  // Create build directory if it doesn't exist
  const buildDir = join(__dirname, "..", "..", "build");
  mkdirSync(buildDir, { recursive: true });

  const dbPath = join(buildDir, `${runId}.db`);

  // Remove existing database if it exists
  if (existsSync(dbPath)) {
    console.log(`Removing existing database: ${dbPath}`);
    unlinkSync(dbPath);
  }

  console.log(`Creating SQLite database: ${dbPath}`);
  console.log(`Storing messages with X-RUN-ID: ${runId}\n`);

  // Create SQLite database
  const db = new Database(dbPath);

  // Create table schema
  db.exec(`
    CREATE TABLE IF NOT EXISTS messages (
      id TEXT PRIMARY KEY,
      "table" TEXT,
      op TEXT,
      after TEXT,
      before TEXT
    )
  `);

  const insertStmt = db.prepare(`
    INSERT OR REPLACE INTO messages (id, "table", op, after, before)
    VALUES (?, ?, ?, ?, ?)
  `);

  const kafka = new Kafka({
    clientId: "petclinic-store",
    brokers: KAFKA_BROKERS,
  });

  const consumer = kafka.consumer({
    groupId: `${GROUP_ID}-store-${Date.now()}`,
  });

  let storedCount = 0;

  try {
    await consumer.connect();
    console.log("Connected to Kafka");

    await consumer.subscribe({
      topic: TOPIC,
      fromBeginning: true,
    });
    console.log(`Subscribed to topic: ${TOPIC}`);
    console.log("Reading and storing messages...\n");

    let lastMessageTime = Date.now();
    const TIMEOUT_MS = 3000;

    await consumer.run({
      eachMessage: async ({ message }) => {
        lastMessageTime = Date.now();

        // Extract X-RUN-ID from headers
        const messageRunId =
          message.headers && message.headers["X-RUN-ID"]
            ? message.headers["X-RUN-ID"].toString()
            : null;

        // Filter by X-RUN-ID
        if (messageRunId === runId) {
          // Extract key
          const key = message.key ? JSON.parse(message.key.toString()) : null;
          const id = key?.id ? String(key.id) : null;

          // Extract payload
          const payload = message.value
            ? JSON.parse(message.value.toString())
            : null;
          const sourceTable = payload?.source?.table || null;
          const op = payload?.op || null;
          const after = payload?.after ? JSON.stringify(payload.after) : null;
          const before = payload?.before
            ? JSON.stringify(payload.before)
            : null;

          if (id) {
            insertStmt.run(id, sourceTable, op, after, before);
            storedCount++;

            if (storedCount % 10 === 0) {
              process.stdout.write(`\rStored ${storedCount} messages...`);
            }
          }
        }
      },
    });

    // Wait for messages to finish
    while (Date.now() - lastMessageTime < TIMEOUT_MS) {
      await new Promise((resolve) => setTimeout(resolve, 500));
    }

    await consumer.disconnect();
    db.close();

    console.log(
      `\n\n═══════════════════════════════════════════════════════════════════`
    );
    console.log(`Successfully stored ${storedCount} messages to ${dbPath}`);
    console.log(
      `═══════════════════════════════════════════════════════════════════\n`
    );

    process.exit(0);
  } catch (error) {
    console.error("Error storing messages:", error.message);
    db.close();
    await consumer.disconnect();
    process.exit(1);
  }
}

// Get command and argument from command line
const command = process.argv[2];
const arg = process.argv[3];

if (command === "list") {
  listRunIds();
} else if (command === "store") {
  storeMessages(arg);
} else if (command === "log") {
  logMessages(arg);
} else {
  console.error("Usage:");
  console.log(
    "  node petclinic-consumer.js list              - List all distinct RUN-IDs"
  );
  console.log(
    "  node petclinic-consumer.js log <run-id>      - Log messages by RUN-ID"
  );
  console.log(
    "  node petclinic-consumer.js store <run-id>    - Store messages to SQLite DB"
  );
  process.exit(1);
}
