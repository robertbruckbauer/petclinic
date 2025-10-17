import { Component, model } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-icon",
  imports: [RouterLink],
  templateUrl: "./app-icon.html",
  styleUrl: "./app-icon.css",
})
export class AppIcon {
  open = model(false);
}
