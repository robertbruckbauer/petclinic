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
    return "of " + this.visit()?.petItem?.text + " on " + this.visit()?.date;
  }

  formatDuration(iso: string | undefined): string {
    if (!iso) return "-";
    const match = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?$/.exec(iso);
    if (!match) return iso;
    const parts: string[] = [];
    if (match[1])
      parts.push(match[1] + (Number(match[1]) === 1 ? " hour" : " hours"));
    if (match[2])
      parts.push(match[2] + (Number(match[2]) === 1 ? " minute" : " minutes"));
    if (match[3])
      parts.push(match[3] + (Number(match[3]) === 1 ? " second" : " seconds"));
    return parts.length ? parts.join(" ") : "-";
  }
}
