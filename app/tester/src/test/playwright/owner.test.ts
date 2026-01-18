import { test, expect } from "./fixtures";

test.describe("Frontend Availability", () => {
  test("should load home page", async ({ page }) => {
    await page.goto("http://localhost:5050");

    // Verify page loaded successfully
    await expect(page).toHaveTitle("Client");

    console.log("[Test] Home page loaded successfully");
  });
});
