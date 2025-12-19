import { test, expect } from "@playwright/test";

test.describe("Navigation", () => {
  test("should open the home page", async ({ page }) => {
    const path = "/home";
    await page.goto(path);
    await page.waitForURL(path);
  });
  test("should open the help page", async ({ page }) => {
    const path = "/help";
    await page.goto(path);
    await page.waitForURL(path);
  });
});

test.describe("Journey", () => {
  test("Welcome new client with sick animal, assign a time slot and perform treatment", async ({ page }) => {
    // create new client, add a new pet
    await page.goto("/owner");
    // add a visit
    await page.goto("/pet");
    // edit visit, assign vet, add treatment details
    await page.goto("/visit");
  });
});
