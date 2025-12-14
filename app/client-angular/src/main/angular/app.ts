import { Component, signal } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";

@Component({
  selector: "app-root",
  imports: [RouterLink, RouterOutlet],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
})
export class App {
  protected menuVisible = signal(false);
  onMenuToggle(event: Event) {
    event.stopPropagation();
    this.menuVisible.update((value) => !value);
  }
  onMenuClose(event: Event) {
    event.stopPropagation();
    this.menuVisible.set(false);
  }
}
