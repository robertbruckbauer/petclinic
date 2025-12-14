import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class VisitListerPage {
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

  async deleteVisit(visitAt, ownerName, petName) {
    await this.page.once("dialog", (dialog) => dialog.accept());
    const row = this.page
      .locator("tr", { hasText: petName })
      .filter({ hasText: ownerName });
    await row.waitFor({ state: "visible" });
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await deleteButton.click();
  }
}
