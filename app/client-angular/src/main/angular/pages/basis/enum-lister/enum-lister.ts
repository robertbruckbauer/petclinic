import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { SpinnerComponent } from "../../../controls/spinner/spinner";
import { EnumService } from "../../../services/enum.service";
import { type EnumItem } from "../../../types/enum.type";

@Component({
  selector: "app-enum-lister",
  imports: [CommonModule, SpinnerComponent],
  templateUrl: "./enum-lister.html",
  styleUrl: "./enum-lister.css",
})
export class EnumListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private restApi = inject(EnumService);
  art = input.required<string>();
  loading = signal(false);
  allItem = signal<EnumItem[]>([]);

  ngOnInit() {
    this.loading.set(true);
    const subscription = this.restApi.loadAllEnum(this.art()).subscribe({
      next: (res) => {
        this.allItem.set(res.content);
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
