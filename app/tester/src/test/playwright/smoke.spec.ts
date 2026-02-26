import { test, expect } from "./fixtures";
import { existsSync } from "node:fs";

test.describe("Phase 2 - Database validation", () => {
  test("should have SQLite database in build directory", ({ sqliteFile }) => {
    expect(existsSync(sqliteFile)).toBe(true);
  });
});
