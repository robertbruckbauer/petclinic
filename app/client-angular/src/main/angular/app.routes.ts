import { Routes } from "@angular/router";
import { HelpComponent } from "./pages/help/help";
import { HomeComponent } from "./pages/home/home";
import { VersionService } from "./services/version.service";

export const backendUrl = () => {
  return (
    window.location.protocol +
    "//" +
    window.location.host.replace("5052", "8080")
  );
};

export const routes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "home" },
  { path: "help", component: HelpComponent },
  { path: "home", component: HomeComponent, providers: [VersionService] },
  {
    path: "enum",
    loadChildren: () =>
      import("./pages/basis/enum.routes").then((m) => m.routes),
  },
  {
    path: "owner",
    loadChildren: () =>
      import("./pages/clinic/owner.routes").then((m) => m.routes),
  },
  {
    path: "pet",
    loadChildren: () =>
      import("./pages/clinic/pet.routes").then((m) => m.routes),
  },
  {
    path: "**",
    loadComponent: () =>
      import("./pages/error/not-found").then((m) => m.NotFoundComponent),
  },
];
