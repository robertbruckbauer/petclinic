import { test } from "@playwright/test";
import { EnumPage } from "./pages/EnumPage.js";
import { OwnerPage } from "./pages/OwnerPage.js";
import { PetPage } from "./pages/PetPage.js";
import { VetPage } from "./pages/VetPage.js";
import { VisitPage } from "./pages/VisitPage.js";

test.describe("Regression", () => {
  test("Home", async ({ page }) => {
    await page.goto("/");
  });

  ["Skill", "Species"].forEach((art) => {
    test(art, async ({ page }) => {
      const enumPage = new EnumPage(page, art);
      await enumPage.goto();
      const name = await enumPage.createItem();
      await enumPage.updateText(name);
      await enumPage.deleteItem(name);
    });
  });

  test("Owner", async ({ page }) => {
    const owner = new OwnerPage(page);
    await owner.goto();
    const ownerName = await owner.createOwner();
    await owner.updateAddress(ownerName);
    await owner.deleteOwner(ownerName);
  });

  test("Pet", async ({ page }) => {
    const pet = new PetPage(page);
    await pet.goto();
  });

  test("Vet", async ({ page }) => {
    const vet = new VetPage(page);
    await vet.goto();
  });

  test("Visit", async ({ page }) => {
    const visit = new VisitPage(page);
    await visit.goto();
  });
});
