import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { forkJoin } from "rxjs";
import { EnumService } from "../../../services/enum.service";
import { OwnerService } from "../../../services/owner.service";
import { mapPetToPetItem } from "../../../services/pet.service";
import { type EnumItem } from "../../../types/enum.type";
import { type Owner } from "../../../types/owner.type";
import { type Pet } from "../../../types/pet.type";
import { OwnerEditorComponent } from "../owner-editor/owner-editor";
import { PetEditorComponent } from "../pet-editor/pet-editor";
import { VisitOverviewComponent } from "../../clinic/visit-overview/visit-overview";

@Component({
  selector: "app-owner-lister",
  imports: [
    CommonModule,
    ReactiveFormsModule,
    OwnerEditorComponent,
    PetEditorComponent,
    VisitOverviewComponent,
  ],
  templateUrl: "./owner-lister.html",
  styles: ``,
})
export class OwnerListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private enumService = inject(EnumService);
  private ownerService = inject(OwnerService);
  loading = signal(false);

  filterForm = new FormGroup({
    criteria: new FormControl("", Validators.required),
  });

  allOwner = signal<Owner[]>([]);
  afterCreateOwner(newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return [newOwner, ...allOwner];
    });
  }
  afterUpdateOwner(newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return allOwner.map((owner) =>
        owner.id === newOwner.id ? newOwner : owner
      );
    });
  }
  afterRemoveOwner(newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return allOwner.filter((owner) => owner.id !== newOwner.id);
    });
  }
  afterCreatePet(newPet: Pet, newOwner: Owner) {
    this.allOwner.update((allOwner) => {
      return allOwner.map((owner) => {
        if (newOwner.id === owner.id) {
          owner.allPetItem.push(mapPetToPetItem(newPet));
        }
        return owner;
      });
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

  newPet = computed<Pet>(() => {
    return {
      version: 0,
      owner: "/api/owner/" + this.ownerId(),
      name: "",
      born: "",
      species: "",
    };
  });

  allSpeciesEnum = signal<EnumItem[]>([]);
  ngOnInit() {
    this.loading.set(true);
    const search = { sort: "name,asc" };
    const subscription = forkJoin({
      allSpeciesEnum: this.enumService.loadAllEnum("species"),
      allOwner: this.ownerService.loadAllOwner(search),
    }).subscribe({
      next: (value) => {
        this.allSpeciesEnum.set(value.allSpeciesEnum);
        this.allOwner.set(value.allOwner);
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
    const search = { sort: "name,asc", name: this.filterForm.value.criteria! };
    const subscription = this.ownerService.loadAllOwner(search).subscribe({
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

  readonly ownerFilterDisabled = computed(
    () =>
      this.ownerEditorCreate() ||
      this.ownerEditorUpdate() ||
      this.petEditorCreate() ||
      this.visitLister()
  );

  readonly ownerEditorDisabled = computed(() => this.ownerFilterDisabled());

  onOwnerRemoveClicked(owner: Owner) {
    this.ownerId.set(undefined); // no owner selected
    const text = owner.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete owner '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.ownerService.removeOwner(owner.id!).subscribe({
      next: (owner) => {
        this.afterRemoveOwner(owner);
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
