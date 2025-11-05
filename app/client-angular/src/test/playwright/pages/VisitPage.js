import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class VisitPage {
  constructor(page) {
    this.page = page;
  }

  path = "/visit";

  async goto() {
    await this.page.goto("/");
    await this.page.locator("header").locator("summary").click();
    await this.page.getByRole("link", { name: "Visit", exact: true }).click();
    await this.page.waitForURL(this.path);
  }
}
