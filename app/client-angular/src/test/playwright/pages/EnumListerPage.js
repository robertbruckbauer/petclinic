import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class EnumListerPage {
  constructor(page, art) {
    this.page = page;
    this.art = art;
    this.path = "/enum/" + art.toLowerCase();
  }

  art;
  path;

  async goto() {
    await this.page.goto("/");
    await this.page.locator("header").locator("summary").click();
    await this.page.getByRole("link", { name: this.art, exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createItem() {
    await expect(this.page).toHaveURL(this.path);
    const name = "Zzz" + chance.word({ length: 5 });
    const text = chance.sentence();
    // Search
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill(name);
    await filterInput.press("Enter");
    await filterInput.press("Tab");
    await this.page.locator(".circle").waitFor({ state: "hidden" });
    // Add
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    // Code
    const codeInput = this.page.locator('[aria-label="Code"]');
    await expect(codeInput).not.toBeEmpty();
    await expect(codeInput).not.toHaveAttribute("readonly", "");
    // Name
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toBeEmpty();
    await nameInput.fill(name);
    await nameInput.press("Tab");
    await expect(nameInput).toHaveValue(name);
    // Text
    const textInput = this.page.locator('[aria-label="Text"]');
    await expect(textInput).toBeEmpty();
    await textInput.fill(text);
    await textInput.press("Tab");
    await expect(textInput).toHaveValue(text);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
    return name;
  }

  async updateText(name) {
    await expect(this.page).toHaveURL(this.path);
    const text = chance.sentence();
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
    // Code
    const codeInput = this.page.locator('[aria-label="Code"]');
    await expect(codeInput).not.toBeEmpty();
    await expect(codeInput).toHaveAttribute("readonly", "");
    // Text
    const textInput = this.page.locator('[aria-label="Text"]');
    await expect(textInput).not.toHaveValue(text);
    await textInput.fill(text);
    await textInput.press("Tab");
    await expect(textInput).toHaveValue(text);
    // Ok
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async deleteItem(name) {
    await expect(this.page).toHaveURL(this.path);
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
    // Delete
    const deleteButton = row
      .getByRole("button")
      .getByText("delete", { exact: true });
    await expect(deleteButton).toBeEnabled();
    await this.page.once("dialog", (dialog) => dialog.accept());
    await deleteButton.click();
  }
}
