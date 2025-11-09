import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { HttpParams } from "@angular/common/http";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { forkJoin } from "rxjs";
import { VetService } from "../../../services/vet.service";
import { VisitService } from "../../../services/visit.service";
import { type VetItem } from "../../../types/vet.type";
import { type Visit } from "../../../types/visit.type";
import { VisitDiagnoseComponent } from "../visit-diagnose/visit-diagnose";

@Component({
  selector: "app-visit-lister",
  imports: [CommonModule, ReactiveFormsModule, VisitDiagnoseComponent],
  templateUrl: "./visit-lister.html",
  styles: ``,
})
export class VisitListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private vetService = inject(VetService);
  private visitService = inject(VisitService);
  loading = signal(false);

  filterForm = new FormGroup({
    criteria: new FormControl("", Validators.required),
  });

  allVisit = signal<Visit[]>([]);
  afterCreateVisit(newVisit: Visit) {
    this.allVisit.update((allVisit) => {
      return [newVisit, ...allVisit];
    });
  }
  afterUpdateVisit(newVisit: Visit) {
    this.allVisit.update((allVisit) => {
      return allVisit.map((visit) =>
        visit.id === newVisit.id ? newVisit : visit
      );
    });
  }
  afterRemoveVisit(newVisit: Visit) {
    this.allVisit.update((allVisit) => {
      return allVisit.filter((visit) => visit.id !== newVisit.id);
    });
  }

  /**
   * Start swimlane if the date changes
   * in a date ordered array.
   */
  isSwimlaneChange(index: number) {
    if (index) {
      const visit1 = this.allVisit()[index - 1];
      const visit2 = this.allVisit()[index];
      return visit1.date !== visit2.date;
    } else {
      return true;
    }
  }

  newVisit = computed<Visit>(() => {
    return {
      version: 0,
      date: "",
      text: "",
    };
  });

  allVetItem = signal<VetItem[]>([]);
  ngOnInit() {
    this.loading.set(true);
    const params = new HttpParams().set("sort", "date,asc");
    const subscription = forkJoin({
      allVetItem: this.vetService.loadAllVetItem(),
      allVisit: this.visitService.loadAllVisit(params),
    }).subscribe({
      next: (value) => {
        this.allVetItem.set(value.allVetItem);
        this.allVisit.set(value.allVisit);
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  onFilterClicked() {
    this.loading.set(true);
    const params = new HttpParams().set("sort", "date,desc");
    const subscription = this.visitService.loadAllVisit(params).subscribe({
      next: (allVisit) => {
        this.allVisit.set(allVisit);
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  visitId = signal<string | undefined>(undefined); // no visit selected
  onVisitClicked(visit: Visit) {
    this.visitId.set(visit.id);
  }

  visitEditorCreate = signal(false);
  onVisitEditorCreateClicked() {
    this.visitId.set(undefined); // no visit selected
    this.visitEditorCreate.set(true);
    this.visitEditorUpdate.set(false);
  }

  visitEditorUpdate = signal(false);
  onVisitEditorUpdateClicked(visit: Visit) {
    this.visitId.set(visit.id);
    this.visitEditorCreate.set(false);
    this.visitEditorUpdate.set(true);
  }

  readonly visitFilterDisabled = computed(
    () => this.visitEditorCreate() || this.visitEditorUpdate()
  );

  readonly visitEditorDisabled = computed(() => this.visitFilterDisabled());

  onVisitRemoveClicked(visit: Visit) {
    this.visitId.set(undefined); // no visit selected
    const hint = visit.date;
    if (!confirm("Delete visit '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.visitService.removeVisit(visit.id!).subscribe({
      next: (visit) => {
        this.afterRemoveVisit(visit);
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
}
