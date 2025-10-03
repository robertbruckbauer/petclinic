import { test } from "@playwright/test";

test.describe("Regression", () => {
  test("Home", async ({ page }) => {
    await page.goto("/");
  });
});
