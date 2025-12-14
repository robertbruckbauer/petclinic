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
  name;
  text;

  async goto() {
    await this.page.goto("/");
    await this.page.locator("header").locator("summary").click();
    await this.page.getByRole("link", { name: this.art, exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createItem() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill("Zzz");
    await filterInput.press("Enter");
    await filterInput.blur();
    await this.page.locator(".circle").waitFor({ state: "hidden" });
    const row = this.page.locator("th");
    const addButton = row.getByRole("button", { name: "add", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    const codeInput = this.page.locator('[aria-label="Code"]');
    await expect(codeInput).not.toBeEmpty();
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toBeEmpty();
    this.name = "Zzz" + chance.word({ length: 5 });
    await nameInput.fill(this.name);
    await expect(nameInput).toHaveValue(this.name);
    const textInput = this.page.locator('[aria-label="Text"]');
    await expect(textInput).toBeEmpty();
    this.text = chance.sentence();
    await textInput.fill(this.text);
    await expect(textInput).toHaveValue(this.text);
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async updateName() {
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill("Zzz");
    await filterInput.press("Enter");
    await filterInput.blur();
    await this.page.locator(".circle").waitFor({ state: "hidden" });
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: this.name });
    await row.waitFor({ state: "visible" });
    const editButton = row.getByRole("button", { name: "edit", exact: true });
    await expect(editButton).toBeEnabled();
    await editButton.click();
    const nameInput = this.page.locator('[aria-label="Name"]');
    await expect(nameInput).toHaveValue(this.name);
    this.name = "Zzz" + chance.word({ length: 5 });
    await nameInput.fill(this.name);
    await expect(nameInput).toHaveValue(this.name);
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }

  async deleteItem() {
    await this.page.once("dialog", (dialog) => dialog.accept());
    await expect(this.page).toHaveURL(this.path);
    const filterInput = this.page.locator('[aria-label="Filter"]');
    await filterInput.fill("Zzz");
    await filterInput.press("Enter");
    await filterInput.blur();
    await this.page.locator(".circle").waitFor({ state: "hidden" });
    const row = this.page
      .getByRole("table")
      .getByRole("row")
      .filter({ hasText: this.name });
    await row.waitFor({ state: "visible" });
    const deleteButton = row
      .getByRole("button")
      .getByText("delete", { exact: true });
    await expect(deleteButton).toBeEnabled();
    await deleteButton.click();
  }
}
