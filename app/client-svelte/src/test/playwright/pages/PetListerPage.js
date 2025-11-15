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
    await this.page.getByRole("button", { name: "Icon" }).click();
    await this.page.getByRole("link", { name: "Pet", exact: true }).click();
    await this.page.waitForURL(this.path);
  }

  async createVisit(visitAt, ownerName, petName) {
    await expect(this.page).toHaveURL(this.path);
    const ownerSelect = this.page.getByLabel("Owner");
    const ownerValue = await ownerSelect.evaluate((element, name) => {
      return Array.from(element.options).find((option) =>
        option.label.startsWith(name)
      )?.value;
    }, ownerName);
    await ownerSelect.selectOption({ value: ownerValue });
    const row = this.page.locator("tr", { hasText: petName });
    await row.waitFor({ state: "visible" });
    const addButton = row.getByRole("button", { name: "event", exact: true });
    await expect(addButton).toBeEnabled();
    await addButton.click();
    const dateField = this.page.getByRole("textbox", {
      name: "Treatment",
    });
    await dateField.fill(visitAt);
    await dateField.press("Tab");
    const okButton = this.page.getByRole("button", { name: "Ok", exact: true });
    await expect(okButton).toBeEnabled();
    await okButton.click();
  }
}
