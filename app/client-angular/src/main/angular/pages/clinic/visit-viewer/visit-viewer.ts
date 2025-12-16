import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  signal,
} from "@angular/core";
import { forkJoin } from "rxjs";
import { Toast } from "../../../controls/toast/toast";
import { VetService } from "../../../services/vet.service";
import { VisitService } from "../../../services/visit.service";
import { type VetItem } from "../../../types/vet.type";
import { type Visit } from "../../../types/visit.type";
import { VisitDiagnoseComponent } from "../visit-diagnose/visit-diagnose";
import { VisitTreatmentComponent } from "../visit-treatment/visit-treatment";

@Component({
  selector: "app-visit-viewer",
  imports: [VisitDiagnoseComponent, VisitTreatmentComponent],
  templateUrl: "./visit-viewer.html",
  styles: ``,
})
export class VisitViewerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private vetService = inject(VetService);
  private visitService = inject(VisitService);
  loading = signal(false);
  visitId = input.required<string>();
  visit = signal<Visit | undefined>(undefined);
  allVetItem = signal<VetItem[]>([]);
  ngOnInit(): void {
    this.loading.set(true);
    const subscription = forkJoin({
      visit: this.visitService.loadOneVisit(this.visitId()),
      allVetItem: this.vetService.loadAllVetItem(),
    }).subscribe({
      next: (value) => {
        this.visit.set(value.visit);
        this.allVetItem.set(value.allVetItem);
      },
      complete: () => {
        this.loading.set(false);
      },
      error: (err) => {
        this.toast.push(err);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  get title() {
    return this.visit()?.petItem?.text + " on " + this.visit()?.date;
  }
}
