import { Routes } from "@angular/router";
import { EnumService } from "../../services/enum.service";
import { VetService } from "../../services/vet.service";
import { VetListerComponent } from "./vet-lister/vet-lister";
import { VetViewerComponent } from "./vet-viewer/vet-viewer";

export const routes: Routes = [
  {
    path: "",
    component: VetListerComponent,
    providers: [EnumService, VetService],
  },
  {
    path: ":vetId",
    component: VetViewerComponent,
    providers: [],
  },
];
