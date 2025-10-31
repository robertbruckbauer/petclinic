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
    const ownerPage = new OwnerPage(page);
    await ownerPage.goto();
    const ownerName = await ownerPage.createOwner();
    await ownerPage.updateAddress(ownerName);
    const petPage = new PetPage(page);
    await petPage.goto();
    await petPage.show(ownerName, []);
    await ownerPage.goto();
    const catName = await ownerPage.createPet(ownerName, "Cat", "2022-04-22");
    await petPage.goto();
    await petPage.show(ownerName, [catName]);
    await ownerPage.goto();
    await ownerPage.deleteOwner(ownerName);
  });

  test("Pet", async ({ page }) => {
    const ownerPage = new OwnerPage(page);
    await ownerPage.goto();
    const ownerName = await ownerPage.createOwner();
    const petPage = new PetPage(page);
    await petPage.goto();
    await petPage.show(ownerName, []);
    const dogName = await petPage.createPet(ownerName, "Dog", "2022-03-09");
    await petPage.updateBorn(ownerName, dogName, "2023-03-09");
    await petPage.show(ownerName, [dogName]);
    await petPage.deletePet(ownerName, dogName);
    await petPage.show(ownerName, []);
    await ownerPage.goto();
    await ownerPage.deleteOwner(ownerName);
  });

  test("Vet", async ({ page }) => {
    const vetPage = new VetPage(page);
    await vetPage.goto();
  });

  test("Visit", async ({ page }) => {
    const visitPage = new VisitPage(page);
    await visitPage.goto();
  });
});
