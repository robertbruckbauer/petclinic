import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class OwnerListerPage {
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
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toHaveValue("");
    await nameInput.fill(ownerName);
    await nameInput.press("Tab");
    await expect(nameInput).toHaveValue(ownerName);
    // Address
    const addressInput = this.page.locator('[aria-label="Address"]');
    await expect(addressInput).toHaveValue("");
    await addressInput.fill(address);
    await addressInput.press("Tab");
    await expect(addressInput).toHaveValue(address);
    // Contact
    const contactInput = this.page.locator('[aria-label="Contact"]');
    await expect(contactInput).toHaveValue("");
    await contactInput.fill(contact);
    await contactInput.press("Tab");
    await expect(contactInput).toHaveValue(contact);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    await okButton.waitFor({ state: "hidden" });
    return ownerName;
  }

  async filterOwner(ownerName) {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(ownerName);
    await filterInput.press("Enter");
    await filterInput.press("Tab");
  }

  async showVisit(ownerName, petName) {
    await this.filterOwner(ownerName);
    const row = this.page.getByRole("table").getByRole("row").nth(1);
    await row.waitFor({ state: "visible" });
    // List on
    const visitButton = row.getByRole("button", { name: "list", exact: true });
    await expect(visitButton).toBeEnabled();
    await visitButton.click();
    // Card
    const card = this.page.getByRole("table").getByRole("row").nth(2);
    const petInput = card.locator('[aria-label="Pet"]');
    await expect(await petInput.inputValue()).toContain(petName);
    const vetInput = card.locator('[aria-label="Vet"]');
    await expect(vetInput).not.toHaveValue("");
    const treatmentInput = card.locator('[aria-label="Treatment"]');
    await expect(treatmentInput).not.toHaveValue("");
    const diagnoseInput = card.locator('[aria-label="Diagnose"]');
    await expect(diagnoseInput).not.toHaveValue("");
    const editButton = card.getByRole("link", { name: "Edit", exact: true });
    await expect(editButton).toBeEnabled();
    // List off
    await expect(visitButton).toBeEnabled();
    await visitButton.click();
  }

  async createPet(ownerName, species, sex, born) {
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
    const speciesSelect = this.page.locator('[aria-label="Species"]');
    await expect(speciesSelect).toHaveValue("");
    await speciesSelect.selectOption(species);
    await speciesSelect.press("Tab");
    await expect(speciesSelect).toHaveValue(species);
    // Sex
    const sexSelect = this.page.locator('[aria-label="Sex"]');
    await sexSelect.selectOption(sex);
    await sexSelect.press("Tab");
    await expect(sexSelect).toHaveValue(sex);
    // Name
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toHaveValue("");
    await nameInput.fill(petName);
    await nameInput.press("Tab");
    await expect(nameInput).toHaveValue(petName);
    // Born
    const bornInput = this.page.locator('[aria-label="Born"]');
    await expect(bornInput).toHaveValue("");
    await bornInput.fill(born);
    await bornInput.press("Tab");
    await expect(bornInput).toHaveValue(born);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    await okButton.waitFor({ state: "hidden" });
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
    await okButton.waitFor({ state: "hidden" });
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
    await deleteButton.waitFor({ state: "hidden" });
  }
}
