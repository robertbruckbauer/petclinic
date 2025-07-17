import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class OwnerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/owner";
  ownerName = "Zzz" + chance.last();
  petName = "Zzz" + chance.first();

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Menü" }).click();
    await this.page.getByRole("link", { name: "Owner" }).click();
    await this.page.waitForURL(this.path);
  }

  async create() {
    await expect(this.page).toHaveURL(this.path);
    await this.page.getByRole("button", { name: "Add a new owner" }).click();
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(this.ownerName);
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

  async addPet() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.ownerName);
    await filterInput.press("Enter");
    await this.page.getByRole("button", { name: "Add a new pet" }).click();
    const speciesSelect = this.page.getByLabel("Species");
    await speciesSelect.selectOption("0");
    await speciesSelect.press("Tab");
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(this.petName);
    await nameField.press("Tab");
    const bornField = this.page.getByRole("textbox", { name: "Born" });
    await bornField.fill("2022-03-22");
    await bornField.press("Tab");
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async delete() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.ownerName);
    await filterInput.press("Enter");
    await this.page.getByRole("button", { name: "Edit owner details" }).click();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await this.page.getByRole("button", { name: "Löschen" }).click();
  }
}
