import { Routes } from "@angular/router";
import { OwnerService } from "../../services/owner.service";
import { EnumService } from "../../services/enum.service";
import { PetService } from "../../services/pet.service";
import { VisitService } from "../../services/visit.service";
import { PetListerComponent } from "./pet-lister/pet-lister";

export const routes: Routes = [
  {
    path: "",
    component: PetListerComponent,
    providers: [EnumService, OwnerService, PetService, VisitService],
  },
];
