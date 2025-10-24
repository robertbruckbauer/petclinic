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
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { EnumService, filterByCriteria } from "../../../services/enum.service";
import { type EnumItem } from "../../../types/enum.type";
import { EnumEditorComponent } from "../enum-editor/enum-editor";

@Component({
  selector: "app-enum-lister",
  imports: [CommonModule, ReactiveFormsModule, EnumEditorComponent],
  templateUrl: "./enum-lister.html",
  styles: ``,
})
export class EnumListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private restApi = inject(EnumService);
  art = input.required<string>();
  loading = signal(false);

  allItem = signal<EnumItem[]>([]);
  afterCreateItem(newItem: EnumItem) {
    this.allItem.update((allItem) => {
      return [newItem, ...allItem];
    });
  }
  afterUpdateItem(newItem: EnumItem) {
    this.allItem.update((allItem) => {
      return allItem.map((item) =>
        item.code === newItem.code ? newItem : item
      );
    });
  }
  afterRemoveItem(newItem: EnumItem) {
    this.allItem.update((allItem) => {
      return allItem.filter((item) => item.code !== newItem.code);
    });
  }

  filterForm = new FormGroup({
    criteria: new FormControl("", Validators.required),
  });

  allFilteredItem = computed(() => {
    return this.allItem().filter(
      filterByCriteria(this.filterForm.value.criteria)
    );
  });

  newItem = computed<EnumItem>(() => {
    return {
      code: Math.max(...this.allItem().map((item) => item.code)) + 1,
      name: "",
      text: "",
    };
  });

  ngOnInit() {
    this.onFilterClicked();
  }

  onFilterClicked() {
    this.loading.set(true);
    const subscription = this.restApi.loadAllEnum(this.art()).subscribe({
      next: (allItem) => {
        this.allItem.set(allItem);
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  itemCode = signal(-1); // no item selected
  onItemClicked(item: EnumItem) {
    this.itemCode.set(item.code);
  }

  itemEditorCreate = signal(false);
  onItemEditorCreateClicked() {
    this.itemCode.set(-1); // no item selected
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

  onItemRemoveClicked(item: EnumItem) {
    this.itemCode.set(-1); // no item selected
    const text = item.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete enum '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.restApi
      .removeEnum(this.art(), item.code)
      .subscribe({
        next: (item) => {
          this.afterRemoveItem(item);
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
