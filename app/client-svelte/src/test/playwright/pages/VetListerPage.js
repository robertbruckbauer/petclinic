import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class VetListerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/vet";

  async goto() {
    await this.page.goto("/");
    await this.page.locator("header").locator("summary").click();
    await this.page.getByRole("link", { name: "Vet", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createVet() {
    await expect(this.page).toHaveURL(this.path);
    const vetName = "Zzz" + chance.word({ syllables: 3 });
    // Add
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    // Name
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toHaveValue("");
    await nameInput.fill(vetName);
    await nameInput.press("Tab");
    await expect(nameInput).toHaveValue(vetName);
    // Skills
    const skillsSelect = this.page.locator('[aria-label="Skills"]');
    await expect(skillsSelect).toHaveValues([]);
    await skillsSelect.selectOption(["Radiology"]);
    await skillsSelect.press("Tab");
    await expect(skillsSelect).toHaveValues([/Radiology/]);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return vetName;
  }

  async filterVet(vetName) {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(vetName);
    await filterInput.press("Enter");
    await filterInput.press("Tab");
  }

  async updateSkills(vetName) {
    await this.filterVet(vetName);
    // Edit
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: vetName });
    const editButton = row.getByRole("button", { name: "edit", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    // Skills
    const skillsSelect = this.page.locator('[aria-label="Skills"]');
    await expect(skillsSelect).not.toHaveValues([]);
    await skillsSelect.selectOption(["Surgery"]);
    await skillsSelect.press("Tab");
    await expect(skillsSelect).toHaveValues([/Surgery/]);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async deleteVet(vetName) {
    await this.filterVet(vetName);
    // Delete
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: vetName });
    const deleteButton = row.getByRole("button", {
      name: "delete",
      exact: true,
    });
    await expect(deleteButton).toBeEnabled();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await deleteButton.click();
  }
}
