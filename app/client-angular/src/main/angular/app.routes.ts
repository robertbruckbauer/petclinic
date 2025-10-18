import { Routes } from "@angular/router";
import { HelpComponent } from "./pages/help/help";
import { HomeComponent } from "./pages/home/home";
import { VersionService } from "./services/version";

export const routes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "home" },
  { path: "help", component: HelpComponent },
  { path: "home", component: HomeComponent, providers: [VersionService] },
  {
    path: "**",
    loadComponent: () =>
      import("./pages/error/not-found").then((m) => m.NotFoundComponent),
  },
];
