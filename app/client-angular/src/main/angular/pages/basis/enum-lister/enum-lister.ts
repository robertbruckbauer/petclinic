import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  input,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { SpinnerComponent } from "../../../controls/spinner/spinner";
import { EnumService } from "../../../services/enum.service";
import { type EnumItem } from "../../../types/enum.type";

@Component({
  selector: "app-enum-lister",
  imports: [CommonModule, ReactiveFormsModule, SpinnerComponent],
  templateUrl: "./enum-lister.html",
  styleUrl: "./enum-lister.css",
})
export class EnumListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private restApi = inject(EnumService);
  art = input.required<string>();
  loading = signal(false);
  allItem = signal<EnumItem[]>([]);

  private formBuilder = inject(FormBuilder);
  filterForm = this.formBuilder.group({
    criteria: ["", Validators.required],
  });

  ngOnInit() {
    this.onFilterClicked();
  }

  onFilterClicked() {
    this.loading.set(true);
    const subscription = this.restApi.loadAllEnum(this.art()).subscribe({
      next: (res) => {
        this.allItem.set(
          res.content.filter((e) => {
            if (this.filterForm.value.criteria) {
              const criteria = this.filterForm.value.criteria.toLowerCase();
              if (e.name.toLowerCase().startsWith(criteria)) return true;
              if (e.text.toLowerCase().startsWith(criteria)) return true;
              return false;
            }
            return true;
          })
        );
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  newItemCode = signal(0);
  itemCode = signal(0);
  onItemClicked(item: EnumItem) {
    this.itemCode.set(item.code);
  }
  onItemRemoveClicked(item: EnumItem) {
    this.itemCode.set(item.code);
    console.log(["onItemRemoveClicked", item]);
  }

  itemEditorCreate = signal(false);
  onItemEditorCreateClicked() {
    this.itemEditorCreate.set(true);
    this.itemEditorUpdate.set(false);
  }

  itemEditorUpdate = signal(false);
  onItemEditorUpdateClicked(item: EnumItem) {
    this.itemCode.set(item.code);
    this.itemEditorCreate.set(false);
    this.itemEditorUpdate.set(true);
  }

  itemEditorDisabled = computed(
    () => this.itemEditorCreate() || this.itemEditorUpdate()
  );
}
