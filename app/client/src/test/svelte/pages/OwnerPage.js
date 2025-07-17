import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class OwnerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/owner";
  name;

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Menü" }).click();
    await this.page.getByRole("link", { name: "Owner" }).click();
    await this.page.waitForURL(this.path);
  }

  async create() {
    await expect(this.page).toHaveURL(this.path);
    await this.page.getByRole("button", { name: "Add a new owner" }).click();
    this.name = "Zzz" + chance.last();
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(this.name);
    await nameField.press("Tab");
    const addressField = this.page.getByRole("textbox", { name: "Address" });
    await addressField.click();
    await addressField.fill("Planet Erde");
    await addressField.press("Tab");
    const contactField = this.page.getByRole("textbox", { name: "Contact" });
    await contactField.click();
    await contactField.fill("Brieftaube");
    await contactField.press("Tab");
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async delete() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.name);
    await filterInput.press("Enter");
    await this.page.getByRole("button", { name: "Edit owner details" }).click();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await this.page.getByRole("button", { name: "Löschen" }).click();
  }
}
