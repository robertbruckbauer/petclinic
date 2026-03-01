import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class VisitListerPage {
  constructor(page) {
    this.page = page;
  }

  path = "/visit";

  async goto() {
    await this.page.goto("/");
    await this.page.locator("header").locator("summary").click();
    await this.page.getByRole("link", { name: "Visit", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async updateDiagnose(ownerName, petName) {
    const text = chance.sentence();
    const row = this.page
      .locator("tr")
      .filter({ hasText: petName })
      .filter({ hasText: ownerName });
    await row.waitFor({ state: "visible" });
    const editButton = row.getByRole("button", { name: "edit", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    // Text
    const textInput = this.page.locator('[aria-label="Diagnose"]');
    await expect(textInput).toHaveValue("");
    await textInput.fill(text);
    await textInput.press("Tab");
    expect(textInput).toHaveValue(text);
    // Vet
    const vetSelect = this.page.locator('[aria-label="Vet"]');
    await expect(vetSelect).toHaveValue("");
    await vetSelect.selectOption({ index: 1 });
    await vetSelect.press("Tab");
    await expect(vetSelect).not.toHaveValue("");
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    await okButton.waitFor({ state: "hidden" });
  }

  async deleteVisit(ownerName, petName) {
    const row = this.page
      .locator("tr")
      .filter({ hasText: petName })
      .filter({ hasText: ownerName });
    await row.waitFor({ state: "visible" });
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
