import { Component, input } from "@angular/core";

@Component({
  selector: "app-icon",
  imports: [],
  templateUrl: "./app-icon.html",
  styleUrl: "./app-icon.css",
})
export class AppIconComponent {
  open = input(false);
}
