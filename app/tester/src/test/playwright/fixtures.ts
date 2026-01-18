import { test as base } from "@playwright/test";

/**
 * Helper function to log entity operations to the tester service
 */
async function logEntity(
  testName: string,
  entityType: string,
  entityId: string
) {
  const testerUrl = process.env.TESTER_URL || "http://localhost:9090";

  try {
    const response = await fetch(`${testerUrl}/log`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        testName,
        entityType,
        entityId,
      }),
    });

    if (!response.ok) {
      console.warn(`Failed to log entity: ${response.status}`);
    }
  } catch (error) {
    console.error("Error logging entity:", error);
  }
}

/**
 * Extended test fixture with entity logging capability
 */
export const test = base.extend<{
  logEntity: (entityType: string, entityId: string) => Promise<void>;
}>({
  logEntity: async ({}, use, testInfo) => {
    const boundLogEntity = (entityType: string, entityId: string) =>
      logEntity(testInfo.title, entityType, entityId);

    await use(boundLogEntity);
  },
});

export { expect } from "@playwright/test";
