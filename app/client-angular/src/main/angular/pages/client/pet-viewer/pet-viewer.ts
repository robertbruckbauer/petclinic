import { Component, input } from "@angular/core";

@Component({
  selector: "app-pet-viewer",
  imports: [],
  templateUrl: "./pet-viewer.html",
  styles: ``,
})
export class PetViewerComponent {
  petId = input.required<string>();

  get title() {
    return this.petId();
  }
}
