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
    await this.page.getByRole("button", { name: "Menü" }).click();
    await this.page.getByRole("link", { name: "Visit", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async deleteVisit(visitAt, ownerName, petName) {
    const petRow = this.page
      .locator("tr", { hasText: petName })
      .filter({ hasText: ownerName });
    await petRow.getByRole("button", { name: "Edit visit details" }).click();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await this.page.getByRole("button", { name: "Löschen" }).click();
  }
}
