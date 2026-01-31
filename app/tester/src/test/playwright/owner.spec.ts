import { test, expect } from "./fixtures";
import { CASE_OWNER_BASIC_CRUD } from "./global-env.js";

test.describe("Phase 2 - Owner CDC validation", () => {
  test(CASE_OWNER_BASIC_CRUD, ({ queryAllEvent }) => {
    const events = queryAllEvent(CASE_OWNER_BASIC_CRUD);
    expect(events.length).toBe(2);
  });
});
