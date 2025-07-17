import { test } from "@playwright/test";
import { OwnerPage } from "./pages/OwnerPage.js";
import { VetPage } from "./pages/VetPage.js";

test.describe("Regression", () => {
  test("Home", async ({ page }) => {
    await page.goto("/");
  });

  test("Owner", async ({ page }) => {
    const owner = new OwnerPage(page);
    await owner.goto();
    await owner.create();
    await owner.addPet();
    await owner.delete();
  });

  test("Vet", async ({ page }) => {
    const vet = new VetPage(page);
    await vet.goto();
    await vet.create();
    await vet.delete();
  });
});
