import { test } from "@playwright/test";
import { EnumListerPage } from "./pages/EnumListerPage.js";
import { OwnerListerPage } from "./pages/OwnerListerPage.js";
import { PetListerPage } from "./pages/PetListerPage.js";
import { VetListerPage } from "./pages/VetListerPage.js";
import { VisitListerPage } from "./pages/VisitListerPage.js";

test.describe("Navigation", () => {
  test("Root", async ({ page }) => {
    await page.goto("/");
    await page.waitForURL("/");
  });
  test("Home", async ({ page }) => {
    const path = "/home";
    await page.goto(path);
    await page.waitForURL(path);
  });
  test("Help", async ({ page }) => {
    const path = "/help";
    await page.goto(path);
    await page.waitForURL(path);
  });
});

test.describe("Enum", () => {
  ["Skill", "Species"].forEach((art) => {
    test(art, async ({ page }) => {
      const enumPage = new EnumListerPage(page, art);
      await enumPage.goto();
      const name = await enumPage.createItem();
      await enumPage.updateText(name);
      await enumPage.deleteItem(name);
    });
  });
});

test.describe("Owner", () => {
  test("OwnerLister", async ({ page }) => {
    const ownerPage = new OwnerListerPage(page);
    await ownerPage.goto();
    const ownerName = await ownerPage.createOwner();
    await ownerPage.updateAddress(ownerName);
    await ownerPage.deleteOwner(ownerName);
  });
});

test.describe("Pet", () => {
  test("PetLister", async ({ page }) => {
    const ownerPage = new OwnerListerPage(page);
    await ownerPage.goto();
    const ownerName = await ownerPage.createOwner();
    const petPage = new PetListerPage(page);
    await petPage.goto();
    await petPage.show(ownerName, []);
    const petName = await petPage.createPet(ownerName, "Dog", "2022-03-09");
    await petPage.show(ownerName, [petName]);
    await petPage.updateBorn(ownerName, petName, "2023-03-09");
    await petPage.deletePet(ownerName, petName);
    await petPage.show(ownerName, []);
    await ownerPage.goto();
    await ownerPage.deleteOwner(ownerName);
  });
});

test.describe("Vet", () => {
  test("VetLister", async ({ page }) => {
    const vetPage = new VetListerPage(page);
    await vetPage.goto();
    const vetName = await vetPage.createVet();
    await vetPage.updateSkills(vetName);
    await vetPage.deleteVet(vetName);
  });
});

test.describe("Visit", () => {
  test("VisitLister", async ({ page }) => {
    const ownerPage = new OwnerListerPage(page);
    await ownerPage.goto();
    const ownerName = await ownerPage.createOwner();
    const petPage = new PetListerPage(page);
    await petPage.goto();
    const petName = await petPage.createPet(ownerName, "Dog", "2022-03-09");
    await petPage.createVisit(ownerName, petName, "2025-04-22");
    const visitPage = new VisitListerPage(page);
    await visitPage.goto();
    await visitPage.updateDiagnose(ownerName, petName);
    await ownerPage.goto();
    await ownerPage.showVisit(ownerName, petName);
    await visitPage.goto();
    await visitPage.deleteVisit(ownerName, petName);
    await petPage.goto();
    await petPage.deletePet(ownerName, petName);
    await ownerPage.goto();
    await ownerPage.deleteOwner(ownerName);
  });
});
