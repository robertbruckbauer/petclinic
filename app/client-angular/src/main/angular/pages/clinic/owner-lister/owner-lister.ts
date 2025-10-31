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
import { OwnerService } from "../../../services/owner.service";
import { type Owner } from "../../../types/owner.type";
import { OwnerEditorComponent } from "../owner-editor/owner-editor";

@Component({
  selector: "app-owner-lister",
  imports: [CommonModule, ReactiveFormsModule, OwnerEditorComponent],
  templateUrl: "./owner-lister.html",
  styles: ``,
})
export class OwnerListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private restApi = inject(OwnerService);
  loading = signal(false);

  filterForm = new FormGroup({
    criteria: new FormControl("", Validators.required),
  });

  allOwner = signal<Owner[]>([]);
  afterCreateItem(newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return [newOwner, ...allOwner];
    });
  }
  afterUpdateItem(newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return allOwner.map((owner) =>
        owner.id === newOwner.id ? newOwner : owner
      );
    });
  }
  afterRemoveItem(newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return allOwner.filter((owner) => owner.id !== newOwner.id);
    });
  }

  newOwner = computed<Owner>(() => {
    return {
      version: 0,
      name: "",
      address: "",
      contact: "",
      allPetItem: [],
    };
  });

  ngOnInit() {
    this.onFilterClicked();
  }

  onFilterClicked() {
    this.loading.set(true);
    const params = new HttpParams()
      .set("sort", "name,asc")
      .set("name", this.filterForm.value.criteria!);
    const subscription = this.restApi.loadAllOwner(params).subscribe({
      next: (allOwner) => {
        this.allOwner.set(allOwner);
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  ownerId = signal<string | undefined>(undefined); // no owner selected
  onOwnerClicked(owner: Owner) {
    this.ownerId.set(owner.id);
  }

  ownerEditorCreate = signal(false);
  onOwnerEditorCreateClicked() {
    this.ownerId.set(undefined); // no owner selected
    this.ownerEditorCreate.set(true);
    this.ownerEditorUpdate.set(false);
    this.petEditorCreate.set(false);
    this.visitLister.set(false);
  }

  ownerEditorUpdate = signal(false);
  onOwnerEditorUpdateClicked(owner: Owner) {
    this.ownerId.set(owner.id);
    this.ownerEditorCreate.set(false);
    this.ownerEditorUpdate.set(true);
    this.petEditorCreate.set(false);
    this.visitLister.set(false);
  }

  petEditorCreate = signal(false);
  onPetCreateEditorClicked(owner: Owner) {
    this.ownerId.set(owner.id);
    this.ownerEditorCreate.set(false);
    this.ownerEditorUpdate.set(false);
    this.petEditorCreate.set(true);
    this.visitLister.set(false);
  }

  visitLister = signal(false);
  onVisitListerClicked(owner: Owner) {
    this.ownerId.set(owner.id);
    this.ownerEditorCreate.set(false);
    this.ownerEditorUpdate.set(false);
    this.petEditorCreate.set(false);
    this.visitLister.set(!this.visitLister());
  }

  ownerEditorDisabled = computed(
    () =>
      this.ownerEditorCreate() ||
      this.ownerEditorUpdate() ||
      this.petEditorCreate() ||
      this.visitLister()
  );

  onOwnerRemoveClicked(owner: Owner) {
    this.ownerId.set(undefined); // no owner selected
    const text = owner.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete enum '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.restApi.removeOwner(owner.id!).subscribe({
      next: (owner) => {
        this.afterRemoveItem(owner);
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
