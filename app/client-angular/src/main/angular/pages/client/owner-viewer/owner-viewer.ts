import { Component, input } from "@angular/core";

@Component({
  selector: "app-owner-viewer",
  imports: [],
  templateUrl: "./owner-viewer.html",
  styles: ``,
})
export class OwnerViewerComponent {
  ownerId = input.required<string>();

  get title() {
    return this.ownerId();
  }
}
