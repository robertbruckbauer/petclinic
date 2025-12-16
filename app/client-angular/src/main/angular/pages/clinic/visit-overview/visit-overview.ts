import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  signal,
} from "@angular/core";
import { RouterLink } from "@angular/router";
import { Toast } from "../../../controls/toast/toast";
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
  private toast = inject(Toast);
  private visitService = inject(VisitService);
  loading = signal(false);

  owner = input.required<Owner>();
  allVisit = signal<Visit[]>([]);

  ngOnInit() {
    this.loading.set(true);
    const search = { sort: "date,desc", "pet.owner.id": this.owner().id! };
    const subscription = this.visitService.loadAllVisit(search).subscribe({
      next: (allVisit) => {
        this.allVisit.set(allVisit);
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
}
