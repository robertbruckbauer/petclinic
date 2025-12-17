import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class PetListerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/pet";

  async goto() {
    await this.page.goto("/");
    await this.page.locator("header").locator("summary").click();
    await this.page.getByRole("link", { name: "Pet", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async filterOwner(ownerName) {
    await expect(this.page).toHaveURL(this.path);
    // Search
    const filterSelect = this.page.getByLabel("Filter");
    await filterSelect.waitFor({ state: "visible" });
    const ownerId = await filterSelect.evaluate((element, name) => {
      return Array.from(element.options).find((option) =>
        option.text.startsWith(name)
      )?.value;
    }, ownerName);
    await filterSelect.selectOption({ value: ownerId });
  }

  async show(ownerName, allPetName) {
    await this.filterOwner(ownerName);
    await Promise.all(
      allPetName.map((petName) => {
        const row = this.page
          .getByRole("table")
          .getByRole("row")
          .filter({ hasText: petName });
        return row.waitFor({ state: "visible" });
      })
    );
  }

  async createPet(ownerName, species, born) {
    const petName = "Zzz" + chance.word({ syllables: 1 });
    // Add
    await this.filterOwner(ownerName);
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    // Species
    const speciesSelect = this.page.locator('[aria-label="Species"]');
    await expect(speciesSelect).toHaveValue("");
    await speciesSelect.selectOption(species);
    await speciesSelect.press("Tab");
    await expect(speciesSelect).toHaveValue(species);
    // Name
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toHaveValue("");
    await nameInput.fill(petName);
    await nameInput.press("Tab");
    expect(nameInput).toHaveValue(petName);
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
    return petName;
  }

  async createVisit(ownerName, petName, date) {
    await this.filterOwner(ownerName);
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: petName });
    await row.waitFor({ state: "visible" });
    // Edit
    const editButton = row.getByRole("button", { name: "event", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    // Treatment
    const dateInput = this.page.locator('[aria-label="Treatment"]');
    await expect(dateInput).not.toHaveValue(date);
    await dateInput.fill(date);
    await dateInput.press("Tab");
    await expect(dateInput).toHaveValue(date);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async updateBorn(ownerName, petName, born) {
    await this.filterOwner(ownerName);
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: petName });
    await row.waitFor({ state: "visible" });
    // Edit
    const editButton = row.getByRole("button", { name: "edit", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    // Born
    const bornInput = this.page.locator('[aria-label="Born"]');
    await expect(bornInput).not.toHaveValue(born);
    await bornInput.fill(born);
    await bornInput.press("Tab");
    await expect(bornInput).toHaveValue(born);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async deletePet(ownerName, petName) {
    await this.filterOwner(ownerName);
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: petName });
    await row.waitFor({ state: "visible" });
    // Delete
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await deleteButton.click();
  }
}
