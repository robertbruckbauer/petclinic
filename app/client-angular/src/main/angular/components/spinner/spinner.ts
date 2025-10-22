import { Component, input } from "@angular/core";

@Component({
  selector: "app-spinner",
  imports: [],
  templateUrl: "./spinner.html",
  styleUrl: "./spinner.css",
})
export class SpinnerComponent {
  color = input("#FF3E00");
  unit = input("px");
  duration = input("0.75s");
  size = input(60);
  pause = input(false);
}
