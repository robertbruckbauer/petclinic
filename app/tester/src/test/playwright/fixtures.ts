import { test as base } from "@playwright/test";
import { readFileSync } from "node:fs";
import { join } from "node:path";
import { DatabaseSync } from "node:sqlite";
import { TESTER_URL } from "./global-env.js";
import { CdcEvent } from "../../main/types/cdc.js";

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
  queryAllEvent: (testName: string) => CdcEvent[];
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

  queryAllEvent: async ({ sqliteFile }, use) => {
    const query = (testName: string): CdcEvent[] => {
      const db = new DatabaseSync(sqliteFile, { readOnly: true });
      try {
        const logStmt = db.prepare(`
          SELECT entity_id, entity_type
          FROM petclinic_log
          WHERE test_name = ?
          LIMIT 1
        `);
        const log = logStmt.get(testName) as
          | { entity_id: string; entity_type: string }
          | undefined;
        console.log(["queryAllEvent", log]);

        if (log) {
          const cdcStmt = db.prepare(`
          SELECT id, timestamp, entity_id, entity_type, operation
          FROM petclinic_cdc
          WHERE entity_id = ?
            AND entity_type = ?
          ORDER BY id ASC
        `);
          return cdcStmt.all(log.entity_id, log.entity_type) as CdcEvent[];
        } else {
          return [];
        }
      } finally {
        db.close();
      }
    };
    await use(query);
  },
});

export { expect } from "@playwright/test";
