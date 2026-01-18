import { fileURLToPath } from "node:url";
import { dirname } from "node:path";

// Get __dirname equivalent in ES modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

/**
 * Global setup for Playwright tests.
 * Invokes the tester service /setup endpoint to initialize the test environment.
 */
async function globalSetup() {
  const testerUrl = process.env.TESTER_URL || "http://localhost:9090";

  console.log("[GlobalSetup] Calling tester service /setup endpoint...");

  try {
    const response = await fetch(`${testerUrl}/setup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(`Setup failed: ${error.error}`);
    }

    const result = await response.json();
    console.log(`[GlobalSetup] Setup complete. RUN-ID: ${result.runId}`);

    // Store RUN-ID for tests
    process.env.RUN_ID = result.runId;
  } catch (error) {
    console.error("[GlobalSetup] Error:", error);
    throw error;
  }
}

export default globalSetup;
