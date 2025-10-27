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

  async createOwner() {
    await expect(this.page).toHaveURL(this.path);
    const ownerName = "Zzz" + chance.word({ syllables: 3 });
    const address = "Planet Erde";
    const contact = "Brieftaube";
    // Add
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    // Name
    const nameField = this.page.getByRole("textbox", { name: "Name" });
    await nameField.click();
    await nameField.fill(ownerName);
    await nameField.press("Tab");
    // Address
    const addressField = this.page.getByRole("textbox", { name: "Address" });
    await addressField.click();
    await addressField.fill(address);
    await addressField.press("Tab");
    // Contact
    const contactField = this.page.getByRole("textbox", { name: "Contact" });
    await contactField.click();
    await contactField.fill(contact);
    await contactField.press("Tab");
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return ownerName;
  }

  async deleteOwner(ownerName) {
    await expect(this.page).toHaveURL(this.path);
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: ownerName });
    await row.waitFor({ state: "visible" });
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await deleteButton.click();
  }
}
