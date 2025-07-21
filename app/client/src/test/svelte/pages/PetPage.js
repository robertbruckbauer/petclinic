import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class PetPage {
  constructor(page) {
    this.page = page;
  }

  path = "/pet";

  async goto() {
    await this.page.goto("/");
    await this.page.getByRole("button", { name: "Menü" }).click();
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
    const petRow = this.page.locator("tr", { hasText: petName });
    await petRow.getByRole("button", { name: "Add a new visit" }).click();
    const dateField = this.page.getByRole("textbox", {
      name: "Date of treatment",
    });
    await dateField.fill(visitAt);
    await dateField.press("Tab");
    await this.page.getByRole("button", { name: "Ok" }).click();
  }
}
