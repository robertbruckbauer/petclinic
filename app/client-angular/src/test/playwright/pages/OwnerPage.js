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
    const nameInput = this.page.getByRole("textbox", { name: "Name" });
    await nameInput.click();
    await nameInput.fill(ownerName);
    await nameInput.press("Tab");
    // Address
    const addressInput = this.page.getByRole("textbox", { name: "Address" });
    await addressInput.click();
    await addressInput.fill(address);
    await addressInput.press("Tab");
    // Contact
    const contactInput = this.page.getByRole("textbox", { name: "Contact" });
    await contactInput.click();
    await contactInput.fill(contact);
    await contactInput.press("Tab");
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return ownerName;
  }

  async updateAddress(name) {
    await expect(this.page).toHaveURL(this.path);
    const address = "Planet Mars";
    // Search
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(name);
    await filterInput.press("Enter");
    await filterInput.press("Tab");
    await this.page.locator(".circle").waitFor({ state: "hidden" });
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: name });
    await row.waitFor({ state: "visible" });
    // Edit
    const editButton = row.getByRole("button", { name: "edit", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    // Addreess
    const addressInput = this.page.locator('[aria-label="Address"]');
    await expect(addressInput).not.toHaveValue(address);
    await addressInput.fill(address);
    await addressInput.press("Tab");
    await expect(addressInput).toHaveValue(address);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
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
