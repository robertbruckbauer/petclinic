import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class OwnerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/owner";

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Icon" }).click();
    await this.page.getByRole("link", { name: "Owner", exact: true }).click();
    await this.page.waitForURL(this.path);
  }
}
