import { Component, model } from "@angular/core";

@Component({
  selector: "app-icon",
  imports: [],
  templateUrl: "./app-icon.html",
  styleUrl: "./app-icon.css",
})
export class AppIcon {
  open = model(false);
}
