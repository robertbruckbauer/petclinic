import { test } from "@playwright/test";
import { OwnerPage } from "./pages/OwnerPage.js";

test.describe("Regression", () => {

  test("Home", async ({ page }) => {
    await page.goto("/");
  });

  test("Owner", async ({ page }) => {
    const owner = new OwnerPage(page);
    await owner.goto();
    await owner.create();
    await owner.delete();
  });
});
