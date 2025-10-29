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
import { distinctUntilChanged, forkJoin, map } from "rxjs";
import { EnumService } from "../../../services/enum.service";
import { OwnerService } from "../../../services/owner.service";
import { PetService } from "../../../services/pet.service";
import { type EnumItem } from "../../../types/enum.type";
import { type OwnerItem } from "../../../types/owner.type";
import { type Pet } from "../../../types/pet.type";
import { PetEditorComponent } from "../pet-editor/pet-editor";
import { toSignal } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-pet-lister",
  imports: [CommonModule, ReactiveFormsModule, PetEditorComponent],
  templateUrl: "./pet-lister.html",
  styles: ``,
})
export class PetListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private enumService = inject(EnumService);
  private ownerService = inject(OwnerService);
  private petService = inject(PetService);
  loading = signal(false);

  filterForm = new FormGroup({
    ownerItem: new FormControl<OwnerItem>(
      { value: "", text: "" },
      Validators.required
    ),
  });
  filterFormValue = toSignal(this.filterForm.valueChanges, {
    initialValue: this.filterForm.value,
  });
  ownerId = computed(() => this.filterFormValue().ownerItem!.value);

  allPet = signal<Pet[]>([]);
  afterCreateItem(newPet: Pet) {
    console.log(["afterCreateItem", newPet]);
    this.allPet.update((allPet) => {
      return [newPet, ...allPet];
    });
  }
  afterUpdateItem(newPet: Pet) {
    console.log(["afterUpdateItem", newPet]);
    this.allPet.update((allPet) => {
      return allPet.map((pet) => (pet.id === newPet.id ? newPet : pet));
    });
  }
  afterRemoveItem(newPet: Pet) {
    console.log(["afterRemoveItem", newPet]);
    this.allPet.update((allPet) => {
      return allPet.filter((pet) => pet.id !== newPet.id);
    });
  }

  newPet = computed<Pet>(() => {
    return {
      version: 0,
      owner: ["api", "owner", this.ownerId()].join("/"),
      name: "",
      born: "",
      species: "",
    };
  });

  allSpeciesEnum = signal<EnumItem[]>([]);
  allOwnerItem = signal<OwnerItem[]>([]);
  ngOnInit() {
    this.loading.set(true);
    const subscription = forkJoin({
      allSpeciesEnum: this.enumService.loadAllEnum("species"),
      allOwnerItem: this.ownerService.loadAllOwnerItem(),
    }).subscribe({
      next: (value) => {
        this.allOwnerItem.set(value.allOwnerItem);
        this.allSpeciesEnum.set(value.allSpeciesEnum);
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
    const params = new HttpParams()
      .set("sort", "name,asc")
      .set("owner.id", this.ownerId());
    const subscription = this.petService.loadAllPet(params).subscribe({
      next: (allPet) => {
        this.allPet.set(allPet);
      },
      complete: () => {
        this.loading.set(false);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  petId = signal<string | undefined>(undefined); // no pet selected
  onPetClicked(pet: Pet) {
    this.petId.set(pet.id);
  }

  petEditorCreate = signal(false);
  onPetEditorCreateClicked() {
    this.petId.set(undefined); // no pet selected
    this.petEditorCreate.set(true);
    this.petEditorUpdate.set(false);
    this.visitEditorCreate.set(false);
  }

  petEditorUpdate = signal(false);
  onPetEditorUpdateClicked(pet: Pet) {
    this.petId.set(pet.id);
    this.petEditorCreate.set(false);
    this.petEditorUpdate.set(true);
    this.visitEditorCreate.set(false);
  }

  visitEditorCreate = signal(false);
  onVisitEditorCreateClicked(pet: Pet) {
    this.petId.set(pet.id);
    this.petEditorCreate.set(false);
    this.petEditorUpdate.set(false);
    this.visitEditorCreate.set(true);
  }

  petFilterDisabled = computed(
    () =>
      this.petEditorCreate() ||
      this.petEditorUpdate() ||
      this.visitEditorCreate()
  );

  petEditorDisabled = computed(
    () => this.ownerId() === "" || this.petFilterDisabled()
  );

  onPetRemoveClicked(pet: Pet) {
    this.petId.set(undefined); // no pet selected
    const text = [pet.species, pet.name].join(" ");
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete enum '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.petService.removePet(pet.id!).subscribe({
      next: (pet) => {
        this.afterRemoveItem(pet);
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
