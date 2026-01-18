import { test, expect } from "@playwright/test";
import { readdirSync, existsSync } from "node:fs";
import { join } from "node:path";

test.describe("Phase 2 - Database Validation", () => {
  test("should have SQLite database in build directory", () => {
    const buildDir = join(process.cwd(), "build");

    // Check if build directory exists
    const buildDirExists = existsSync(buildDir);
    console.log(`[Assert] Build directory exists: ${buildDirExists}`);
    expect(buildDirExists).toBe(true);

    // Find SQLite database files
    const files = readdirSync(buildDir).filter((f) => f.endsWith(".sqlite"));

    console.log(`[Assert] Found ${files.length} SQLite database(s)`);
    if (files.length > 0) {
      console.log(`[Assert] Database files: ${files.join(", ")}`);
    }

    expect(files.length).toBeGreaterThan(0);
  });
});
