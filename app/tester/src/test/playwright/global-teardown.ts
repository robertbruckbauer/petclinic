import { TESTER_URL } from "./global-env.js";
import type { TeardownResponse } from "../../main/types/teardown.js";
import type { ErrorResponse } from "../../main/types/error.js";

async function globalTeardown() {
  try {
    const url = `${TESTER_URL}/teardown`;
    console.log(`[GlobalTeardown] URL=${url}`);
    const response = await fetch(`${url}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });
    if (response.ok) {
      const json = (await response.json()) as TeardownResponse;
      console.log(`[GlobalTeardown] RUN-ID=${json.runId}`);
    } else {
      const json = (await response.json()) as ErrorResponse;
      console.warn(`[GlobalTeardown] ${json.error}`);
      return;
    }
  } catch (error) {
    console.error(`[GlobalTeardown] ${error}`);
  }
}

export default globalTeardown;
