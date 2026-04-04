import { fileURLToPath } from "node:url";
import { dirname, join } from "node:path";
import { writeFileSync, mkdirSync } from "node:fs";
import { TESTER_URL } from "./global-env.js";
import type { SetupResponse } from "../../main/types/setup.js";
import type { ErrorResponse } from "../../main/types/error.js";

// Get __dirname equivalent in ES modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

async function globalSetup() {
  try {
    const url = `${TESTER_URL}/setup`;
    console.log(`[GlobalSetup] URL=${url}`);
    const response = await fetch(`${url}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });
    if (response.ok) {
      const json = (await response.json()) as SetupResponse;
      const buildDir = join(__dirname, "..", "..", "..", "build");
      mkdirSync(buildDir, { recursive: true });
      const runIdFile = join(buildDir, "run.id");
      writeFileSync(runIdFile, json.runId, "utf-8");
      console.log(`[GlobalSetup] RUN-ID=${json.runId}`);
    } else {
      const json = (await response.json()) as ErrorResponse;
      console.warn(`[GlobalSetup] ${json.error}`);
      throw new Error(json.error);
    }
  } catch (error) {
    console.error(`[GlobalSetup] ${error}`);
    throw error;
  }
}

export default globalSetup;
