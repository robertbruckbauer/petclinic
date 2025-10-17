import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class VetPage {
  constructor(page) {
    this.page = page;
  }

  path = "/vet";
  vetName = chance.word({ syllables: 3 });

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Icon" }).click();
    await this.page.getByRole("link", { name: "Vet", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createVet() {
    await expect(this.page).toHaveURL(this.path);
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(this.vetName);
    await nameField.press("Tab");
    const skillsSelect = this.page.getByLabel("Skills");
    await skillsSelect.selectOption("Radiology");
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async deleteVet() {
    await this.page.once("dialog", (dialog) => dialog.accept());
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.vetName);
    await filterInput.press("Enter");
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: this.vetName });
    await row.waitFor({ state: "visible" });
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await deleteButton.click();
  }
}
