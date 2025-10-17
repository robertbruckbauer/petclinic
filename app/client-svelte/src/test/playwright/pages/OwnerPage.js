import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class OwnerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/owner";
  ownerName = "Zzz" + chance.word({ syllables: 3 });
  address = "Planet Erde";
  contact = "Brieftaube";
  petName = "Zzz" + chance.word({ syllables: 1 });

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Icon" }).click();
    await this.page.getByRole("link", { name: "Owner", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createOwner() {
    await expect(this.page).toHaveURL(this.path);
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(this.ownerName);
    await nameField.press("Tab");
    const addressField = this.page.getByRole("textbox", { name: "Address" });
    await addressField.click();
    await addressField.fill(this.address);
    await addressField.press("Tab");
    const contactField = this.page.getByRole("textbox", { name: "Contact" });
    await contactField.click();
    await contactField.fill(this.contact);
    await contactField.press("Tab");
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return this.ownerName;
  }

  async createPet() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.ownerName);
    await filterInput.press("Enter");
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: this.ownerName });
    await row.waitFor({ state: "visible" });
    const editButton = row.getByRole("button", { name: "pets", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
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
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return this.petName;
  }

  async deleteOwner() {
    await this.page.once("dialog", (dialog) => dialog.accept());
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(this.ownerName);
    await filterInput.press("Enter");
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: this.ownerName });
    await row.waitFor({ state: "visible" });
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await deleteButton.click();
  }
}
