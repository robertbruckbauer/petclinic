import { test } from "@playwright/test";
import { EnumListerPage } from "./pages/EnumListerPage.js";
import { OwnerListerPage } from "./pages/OwnerListerPage.js";
import { PetListerPage } from "./pages/PetListerPage.js";
import { VetListerPage } from "./pages/VetListerPage.js";
import { VisitListerPage } from "./pages/VisitListerPage.js";

test.describe("Navigation", () => {
  test("Root", async ({ page }) => {
    await page.goto("/");
    // Some deployments don't perform a client-side redirect from "/" to "/home",
    // so wait for a known element on the home page instead of waiting for URL change.
    await page.waitForSelector('h1:has-text("Info")');
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
      await enumPage.updateName();
      await enumPage.deleteItem();
    });
  });
});

test.describe("Owner", () => {
  test("OwnerLister", async ({ page }) => {
    const owner = new OwnerListerPage(page);
    const pet = new PetListerPage(page);
    const visit = new VisitListerPage(page);
    await owner.goto();
    const ownerName = await owner.createOwner();
    const petName = await owner.createPet();
    const visitAt = "2025-04-22";
    await pet.goto();
    await pet.createVisit(visitAt, ownerName, petName);
    await visit.goto();
    await visit.deleteVisit(visitAt, ownerName, petName);
    await owner.goto();
    await owner.deleteOwner();
  });
});

test.describe("Pet", () => {
  test("PetLister", async ({ page }) => {
    const pet = new PetListerPage(page);
    await pet.goto();
  });
});

test.describe("Vet", () => {
  test("VetLister", async ({ page }) => {
    const vet = new VetListerPage(page);
    await vet.goto();
    await vet.createVet();
    await vet.deleteVet();
  });
});

test.describe("Visit", () => {
  test("VisitLister", async ({ page }) => {
    const visit = new VisitListerPage(page);
    await visit.goto();
  });
});
