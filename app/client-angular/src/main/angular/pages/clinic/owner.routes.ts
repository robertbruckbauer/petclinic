import { Routes } from "@angular/router";
import { OwnerService } from "../../services/owner.service";
import { OwnerListerComponent } from "./owner-lister/owner-lister";

export const routes: Routes = [
  { path: "", component: OwnerListerComponent, providers: [OwnerService] },
];
