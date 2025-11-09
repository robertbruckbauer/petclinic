import { Routes } from "@angular/router";
import { EnumService } from "../../services/enum.service";
import { PetService } from "../../services/pet.service";
import { VetService } from "../../services/vet.service";
import { VisitService } from "../../services/visit.service";
import { VisitListerComponent } from "./visit-lister/visit-lister";

export const routes: Routes = [
  {
    path: "",
    component: VisitListerComponent,
    providers: [EnumService, PetService, VetService, VisitService],
  },
];
