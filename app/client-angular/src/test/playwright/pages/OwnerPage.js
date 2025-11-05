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
    await this.page.locator("header").locator("summary").click();
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
    await expect(nameInput).toHaveValue("");
    await nameInput.fill(ownerName);
    await nameInput.press("Tab");
    await expect(nameInput).toHaveValue(ownerName);
    // Address
    const addressInput = this.page.getByRole("textbox", { name: "Address" });
    await expect(addressInput).toHaveValue("");
    await addressInput.fill(address);
    await addressInput.press("Tab");
    await expect(addressInput).toHaveValue(address);
    // Contact
    const contactInput = this.page.getByRole("textbox", { name: "Contact" });
    await expect(contactInput).toHaveValue("");
    await contactInput.fill(contact);
    await contactInput.press("Tab");
    await expect(contactInput).toHaveValue(contact);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return ownerName;
  }

  async filterOwner(ownerName) {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(ownerName);
    await filterInput.press("Enter");
    await filterInput.press("Tab");
  }

  async createPet(ownerName, species, born) {
    const petName = "Zzz" + chance.word({ syllables: 1 });
    await this.filterOwner(ownerName);
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: ownerName });
    await row.waitFor({ state: "visible" });
    // Edit
    const editButton = row.getByRole("button", { name: "pets", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    // Species
    const speciesSelect = this.page.getByLabel("Species");
    await expect(speciesSelect).toHaveValue("");
    await speciesSelect.selectOption(species);
    await speciesSelect.press("Tab");
    await expect(speciesSelect).toHaveValue(species);
    // Name
    const nameInput = this.page.getByRole("textbox", { name: "Name" });
    await expect(nameInput).toHaveValue("");
    await nameInput.fill(petName);
    await nameInput.press("Tab");
    await expect(nameInput).toHaveValue(petName);
    // Born
    const bornInput = this.page.getByRole("textbox", { name: "Born" });
    await expect(bornInput).toHaveValue("");
    await bornInput.fill(born);
    await bornInput.press("Tab");
    await expect(bornInput).toHaveValue(born);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return petName;
  }

  async updateAddress(ownerName) {
    const address = "Planet Mars";
    await this.filterOwner(ownerName);
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: ownerName });
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
    await this.filterOwner(ownerName);
    // Delete
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: ownerName });
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await deleteButton.click();
  }
}
