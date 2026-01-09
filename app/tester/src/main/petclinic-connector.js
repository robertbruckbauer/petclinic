import { randomUUID } from "crypto";

const DEBEZIUM_URL = "http://localhost:9683";
const CONNECTOR_NAME = "petclinic-postgres-connector";

/**
 * Creates a Debezium PostgreSQL source connector that:
 * - Produces all messages to a single topic: petclinic.all
 * - Adds a header X-RUN-ID with a UUID value to each message
 */
async function createDebeziumConnector() {
  const runId = randomUUID();
  console.log(`Creating Debezium connector with RUN-ID: ${runId}`);

  const connectorConfig = {
    name: CONNECTOR_NAME,
    config: {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",

      // Database connection
      "database.hostname": "postgres17",
      "database.port": "5432",
      "database.user": "sa",
      "database.password": "P@ssw0rd",
      "database.dbname": "postgres",
      "database.server.name": "petclinic",

      // Plugin and schema configuration
      "plugin.name": "pgoutput",
      "publication.autocreate.mode": "filtered",
      "schema.include.list": "public",
      "table.include.list": "public.*",

      // Topic routing - route all tables to single topic
      "topic.prefix": "petclinic",
      "topic.creation.default.replication.factor": "1",
      "topic.creation.default.partitions": "1",

      // Use regex router to route all tables to single topic
      transforms: "route,addHeader",
      "transforms.route.type":
        "org.apache.kafka.connect.transforms.RegexRouter",
      "transforms.route.regex": "petclinic\\.public\\.(.*)",
      "transforms.route.replacement": "petclinic.all",

      // Add X-RUN-ID header with UUID
      "transforms.addHeader.type":
        "org.apache.kafka.connect.transforms.InsertHeader",
      "transforms.addHeader.header": "X-RUN-ID",
      "transforms.addHeader.value.literal": runId,

      // Snapshot configuration
      "snapshot.mode": "initial",

      // Key and value converters
      "key.converter": "org.apache.kafka.connect.json.JsonConverter",
      "key.converter.schemas.enable": "false",
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable": "false",

      // Slot configuration
      "slot.name": "debezium_slot",
      "publication.name": "debezium_publication",
    },
  };

  try {
    // Check if connector already exists and delete it
    const checkResponse = await fetch(
      `${DEBEZIUM_URL}/connectors/${CONNECTOR_NAME}`,
      {
        method: "GET",
      }
    );

    if (checkResponse.ok) {
      console.log(`Connector ${CONNECTOR_NAME} already exists. Deleting...`);
      const deleteResponse = await fetch(
        `${DEBEZIUM_URL}/connectors/${CONNECTOR_NAME}`,
        {
          method: "DELETE",
        }
      );

      if (!deleteResponse.ok) {
        throw new Error(
          `Failed to delete existing connector: ${deleteResponse.statusText}`
        );
      }

      console.log("Existing connector deleted successfully");
      // Wait a bit for deletion to complete
      await new Promise((resolve) => setTimeout(resolve, 2000));
    }

    // Create the connector
    console.log("Creating new connector...");
    const createResponse = await fetch(`${DEBEZIUM_URL}/connectors`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(connectorConfig),
    });

    if (!createResponse.ok) {
      const errorText = await createResponse.text();
      throw new Error(
        `Failed to create connector: ${createResponse.statusText}\n${errorText}`
      );
    }

    const result = await createResponse.json();
    console.log("Connector created successfully!");
    console.log(JSON.stringify(result, null, 2));

    // Check connector status
    const statusResponse = await fetch(
      `${DEBEZIUM_URL}/connectors/${CONNECTOR_NAME}/status`
    );
    if (statusResponse.ok) {
      const status = await statusResponse.json();
      console.log("\nConnector Status:");
      console.log(JSON.stringify(status, null, 2));
    }
  } catch (error) {
    console.error("Error creating Debezium connector:", error.message);
    process.exit(1);
  }
}

/**
 * Deletes the Debezium connector
 */
async function deleteDebeziumConnector() {
  console.log(`Deleting Debezium connector: ${CONNECTOR_NAME}`);

  try {
    const checkResponse = await fetch(
      `${DEBEZIUM_URL}/connectors/${CONNECTOR_NAME}`,
      {
        method: "GET",
      }
    );

    if (!checkResponse.ok) {
      console.log(`Connector ${CONNECTOR_NAME} does not exist.`);
      return;
    }

    const deleteResponse = await fetch(
      `${DEBEZIUM_URL}/connectors/${CONNECTOR_NAME}`,
      {
        method: "DELETE",
      }
    );

    if (!deleteResponse.ok) {
      throw new Error(
        `Failed to delete connector: ${deleteResponse.statusText}`
      );
    }

    console.log("Connector deleted successfully!");
  } catch (error) {
    console.error("Error deleting Debezium connector:", error.message);
    process.exit(1);
  }
}

// Run the script based on command line argument
const action = process.argv[2] || "create";

if (action === "delete") {
  deleteDebeziumConnector();
} else if (action === "create") {
  createDebeziumConnector();
} else {
  console.error(`Unknown action: ${action}`);
  console.log("Usage: node create-debezium-connector.js [create|delete]");
  process.exit(1);
}
