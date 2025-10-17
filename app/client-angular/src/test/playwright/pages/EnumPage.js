import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class EnumPage {
  constructor(page, art) {
    this.page = page;
    this.art = art;
    this.path = "/enum/" + art.toLowerCase();
  }

  art;
  path;

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Icon" }).click();
    await this.page.getByRole("link", { name: this.art, exact: true }).click();
    await this.page.waitForURL(this.path);
  }
}
