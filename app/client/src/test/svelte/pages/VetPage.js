import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class VetPage {
  constructor(page) {
    this.page = page;
  }

  path = "/vet";
  name;

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Menü" }).click();
    await this.page.getByRole("link", { name: "Vet", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createVet() {
    await expect(this.page).toHaveURL(this.path);
    await this.page.getByRole("button", { name: "Add a new vet" }).click();
    this.name = "Zzz" + chance.last();
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(this.name);
    await nameField.press("Tab");
    const skillsSelect = this.page.getByLabel("Skills");
    await skillsSelect.selectOption("Radiology");
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async deleteVet() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.name);
    await filterInput.press("Enter");
    await this.page.getByRole("button", { name: "Edit vet details" }).click();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await this.page.getByRole("button", { name: "Löschen" }).click();
  }
}
