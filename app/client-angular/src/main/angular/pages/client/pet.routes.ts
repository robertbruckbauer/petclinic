import { Routes } from "@angular/router";
import { OwnerService } from "../../services/owner.service";
import { EnumService } from "../../services/enum.service";
import { PetService } from "../../services/pet.service";
import { VisitService } from "../../services/visit.service";
import { PetListerComponent } from "./pet-lister/pet-lister";
import { PetViewerComponent } from "./pet-viewer/pet-viewer";

export const routes: Routes = [
  // tag::lister[]
  {
    path: "",
    component: PetListerComponent,
    providers: [EnumService, OwnerService, PetService, VisitService],
  },
  // end::lister[]
  // tag::viewer[]
  {
    path: ":petId",
    component: PetViewerComponent,
    providers: [],
  },
  // end::viewer[]
];
