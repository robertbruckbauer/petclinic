import { test, expect } from "./fixtures";
import { CASE_OWNER_BASIC_CRUD } from "./global-env.js";

test.describe("Phase 2 - Owner CDC validation", () => {
  test(CASE_OWNER_BASIC_CRUD, ({ queryAllEvent }) => {
    const events = queryAllEvent(CASE_OWNER_BASIC_CRUD);
    const operations = events.map((event) => event.operation.toLowerCase());
    expect(events.length).toBe(4);
    expect(operations.filter((operation) => operation === "c")).toHaveLength(1);
    expect(operations.filter((operation) => operation === "u")).toHaveLength(2);
    expect(operations.filter((operation) => operation === "d")).toHaveLength(1);
  });
});
