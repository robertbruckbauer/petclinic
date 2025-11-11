import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  signal,
} from "@angular/core";
import { HttpParams } from "@angular/common/http";
import { RouterLink } from "@angular/router";
import { VisitService } from "../../../services/visit.service";
import { type Owner } from "../../../types/owner.type";
import { type Visit } from "../../../types/visit.type";

@Component({
  selector: "app-visit-overview",
  imports: [RouterLink],
  templateUrl: "./visit-overview.html",
  styles: ``,
})
export class VisitOverviewComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private visitService = inject(VisitService);
  loading = signal(false);

  owner = input.required<Owner>();
  allVisit = signal<Visit[]>([]);

  ngOnInit() {
    this.loading.set(true);
    const params = new HttpParams()
      .set("sort", "date,desc")
      .set("pet.owner.id", this.owner().id!);
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
}
