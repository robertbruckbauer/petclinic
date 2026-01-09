import { test, expect } from "@playwright/test";
import { randomUUID } from "crypto";

const CDC_TOPIC = process.env.CDC_TOPIC ?? "petclinic.all";
const CDC_RUNID = process.env.CDC_RUNID ?? randomUUID();
const CDC_RUNID_KEY = "X-RUN-ID";

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
  const killEventUrl = ["http://localhost:8080", "api", "vet", CDC_RUNID].join(
    "/"
  );

  test.beforeAll(async () => {
    const resCreate = await fetch(killEventUrl, {
      method: "PUT",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ name: "Kill Me" }),
    });
    expect(resCreate.status).toBe(201);
  });

  test.afterAll(async () => {
    const resRemove = await fetch(killEventUrl, { method: "DELETE" });
    expect(resRemove.status).toBe(200);
  });

  test("Welcome new client with sick animal, assign a time slot and perform treatment", async ({
    page,
  }) => {
    // create new client, add a new pet
    await page.goto("/owner");
    // add a visit
    await page.goto("/pet");
    // edit visit, assign vet, add treatment details
    await page.goto("/visit");
  });
});
