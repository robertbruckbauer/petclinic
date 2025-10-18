import { Component, signal } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { AppLogo } from "./components/app-logo/app-logo";
import { AppIcon } from "./components/app-icon/app-icon";

@Component({
  selector: "app-root",
  imports: [AppLogo, AppIcon, RouterLink, RouterOutlet],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
})
export class App {
  protected menuVisible = signal(false);
  onMenuToggle() {
    this.menuVisible.update((value) => !value);
  }
  onMenuClose() {
    this.menuVisible.set(false);
  }
}
