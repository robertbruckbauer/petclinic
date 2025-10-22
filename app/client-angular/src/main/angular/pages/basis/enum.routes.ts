import { Routes } from "@angular/router";
import { EnumService } from "../../services/enum.service";
import { EnumListerComponent } from "./enum-lister/enum-lister";

export const routes: Routes = [
  { path: ":art", component: EnumListerComponent, providers: [EnumService] },
];
