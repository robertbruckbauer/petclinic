import { Component, input } from "@angular/core";

@Component({
  selector: "app-vet-viewer",
  imports: [],
  templateUrl: "./vet-viewer.html",
  styles: ``,
})
export class VetViewerComponent {
  vetId = input.required<string>();

  get title() {
    return this.vetId();
  }
}
