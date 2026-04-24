import { Routes } from "@angular/router";
import { InvoiceService } from "../../services/invoice.service";
import { VisitService } from "../../services/visit.service";
import { InvoiceListerComponent } from "./invoice-lister/invoice-lister";

export const routes: Routes = [
  {
    path: "",
    component: InvoiceListerComponent,
    providers: [InvoiceService, VisitService],
  },
];
