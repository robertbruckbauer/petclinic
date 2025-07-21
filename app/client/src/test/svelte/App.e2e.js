import { test } from "@playwright/test";
import { OwnerPage } from "./pages/OwnerPage.js";
import { PetPage } from "./pages/PetPage.js";
import { VetPage } from "./pages/VetPage.js";
import { VisitPage } from "./pages/VisitPage.js";

test.describe("Regression", () => {
  test("Home", async ({ page }) => {
    await page.goto("/");
  });

  test("Owner", async ({ page }) => {
    const owner = new OwnerPage(page);
    const pet = new PetPage(page);
    const visit = new VisitPage(page);
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

  test("Vet", async ({ page }) => {
    const vet = new VetPage(page);
    await vet.goto();
    await vet.createVet();
    await vet.deleteVet();
  });

  test("Visit", async ({ page }) => {
    const visit = new VisitPage(page);
    await visit.goto();
  });
});
