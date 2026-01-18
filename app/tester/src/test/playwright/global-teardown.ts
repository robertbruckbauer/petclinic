/**
 * Global teardown for Playwright tests.
 * Invokes the tester service /teardown endpoint to clean up the test environment.
 */
async function globalTeardown() {
  const testerUrl = process.env.TESTER_URL || "http://localhost:9090";

  console.log("[GlobalTeardown] Calling tester service /teardown endpoint...");

  try {
    const response = await fetch(`${testerUrl}/teardown`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      const error = await response.json();
      console.warn(`[GlobalTeardown] Teardown warning: ${error.error}`);
      return;
    }

    const result = await response.json();
    console.log(`[GlobalTeardown] Teardown complete. RUN-ID: ${result.runId}`);
  } catch (error) {
    console.error("[GlobalTeardown] Error:", error);
  }
}

export default globalTeardown;
