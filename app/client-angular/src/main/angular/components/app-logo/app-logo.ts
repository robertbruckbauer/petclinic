import { Component, input } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-logo",
  imports: [RouterLink],
  templateUrl: "./app-logo.html",
  styleUrl: "./app-logo.css",
})
export class AppLogo {
  routerLink = input.required<string>();
}
