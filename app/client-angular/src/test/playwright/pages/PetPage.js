import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class PetPage {
  constructor(page) {
    this.page = page;
  }

  path = "/pet";

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Icon" }).click();
    await this.page.getByRole("link", { name: "Pet", exact: true }).click();
    await this.page.waitForURL(this.path);
  }
}
