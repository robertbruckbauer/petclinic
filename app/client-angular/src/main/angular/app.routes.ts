import { Routes } from "@angular/router";
import { HelpComponent } from "./pages/help/help";
import { HomeComponent } from "./pages/home/home";
import { NotFoundComponent } from "./pages/error/not-found";

export const routes: Routes = [
  { path: "", redirectTo: "home", pathMatch: "full" },
  { path: "help", component: HelpComponent },
  { path: "home", component: HomeComponent },
  { path: "**", component: NotFoundComponent },
];
