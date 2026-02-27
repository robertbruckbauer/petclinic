import { test, expect } from "./fixtures";
import { existsSync } from "node:fs";

test.describe("Phase 1 - Database validation", () => {
  test("should have SQLite database in build directory", ({ sqliteFile }) => {
    expect(existsSync(sqliteFile)).toBe(true);
  });
});

test.describe("Phase 1 - Smoketest", () => {
  test("should load angular frontend", async ({ page }) => {
    await page.goto("http://localhost:5052");
    await expect(page).toHaveTitle("Client");
  });
  test("should load svelte frontend", async ({ page }) => {
    await page.goto("http://localhost:5050");
    await expect(page).toHaveTitle("Client");
  });
  test("should load redpanda frontend", async ({ page }) => {
    await page.goto("http://localhost:9645");
    await expect(page).toHaveTitle("Overview - Redpanda Console");
  });
});
