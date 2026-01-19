import { test as base } from "@playwright/test";
import { readFileSync } from "node:fs";
import { join } from "node:path";
import { TESTER_URL } from "./global-env.js";

/**
 * Helper function to log entity operations to the tester service
 */
async function logEntity(
  testName: string,
  entityType: string,
  entityId: string
) {
  try {
    const response = await fetch(`${TESTER_URL}/log`, {
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

export const test = base.extend<{
  logEntity: (entityType: string, entityId: string) => Promise<void>;
  runId: string;
  sqliteFile: string;
}>({
  logEntity: async ({}, use, testInfo) => {
    const boundLogEntity = (entityType: string, entityId: string) =>
      logEntity(testInfo.title, entityType, entityId);
    await use(boundLogEntity);
  },

  runId: async ({}, use) => {
    const buildDir = join(process.cwd(), "build");
    const runIdFile = join(buildDir, "run.id");
    const runId = readFileSync(runIdFile, "utf-8").trim();
    await use(runId);
  },

  sqliteFile: async ({ runId }, use) => {
    const buildDir = join(process.cwd(), "build");
    const sqliteFile = join(buildDir, `${runId}.sqlite`);
    await use(sqliteFile);
  },
});

export { expect } from "@playwright/test";
