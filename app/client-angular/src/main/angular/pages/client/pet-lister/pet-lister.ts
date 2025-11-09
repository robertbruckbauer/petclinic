import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { CommonModule } from "@angular/common";
import { HttpParams } from "@angular/common/http";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { forkJoin } from "rxjs";
import { EnumService } from "../../../services/enum.service";
import { OwnerService } from "../../../services/owner.service";
import { PetService } from "../../../services/pet.service";
import { type EnumItem } from "../../../types/enum.type";
import { type OwnerItem } from "../../../types/owner.type";
import { type Pet } from "../../../types/pet.type";
import { type Visit } from "../../../types/visit.type";
import { PetEditorComponent } from "../pet-editor/pet-editor";
import { VisitTreatmentComponent } from "../../clinic/visit-treatment/visit-treatment";

@Component({
  selector: "app-pet-lister",
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PetEditorComponent,
    VisitTreatmentComponent,
  ],
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
    ownerId: new FormControl("", Validators.required),
  });
  filterFormValue = toSignal(this.filterForm.valueChanges, {
    initialValue: this.filterForm.value,
  });
  ownerId = computed(() => this.filterFormValue().ownerId!);

  allPet = signal<Pet[]>([]);
  afterCreatePet(newPet: Pet) {
    this.allPet.update((allPet) => {
      return [newPet, ...allPet];
    });
  }
  afterUpdatePet(newPet: Pet) {
    this.allPet.update((allPet) => {
      return allPet.map((pet) => (pet.id === newPet.id ? newPet : pet));
    });
  }
  afterRemovePet(newPet: Pet) {
    this.allPet.update((allPet) => {
      return allPet.filter((pet) => pet.id !== newPet.id);
    });
  }

  newPet = computed<Pet>(() => {
    return {
      version: 0,
      owner: "/api/owner/" + this.ownerId(),
      name: "",
      born: "",
      species: "",
    };
  });

  newTreatment = computed<Visit>(() => {
    return {
      version: 0,
      pet: "/api/pet/" + this.petId(),
      date: "",
      text: "",
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
        this.allSpeciesEnum.set(value.allSpeciesEnum);
        this.allOwnerItem.set(value.allOwnerItem);
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
    this.treatmentCreate.set(false);
  }

  petEditorUpdate = signal(false);
  onPetEditorUpdateClicked(pet: Pet) {
    this.petId.set(pet.id);
    this.petEditorCreate.set(false);
    this.petEditorUpdate.set(true);
    this.treatmentCreate.set(false);
  }

  treatmentCreate = signal(false);
  onVisitEditorCreateClicked(pet: Pet) {
    this.petId.set(pet.id);
    this.petEditorCreate.set(false);
    this.petEditorUpdate.set(false);
    this.treatmentCreate.set(true);
  }

  readonly petFilterDisabled = computed(
    () =>
      this.petEditorCreate() || this.petEditorUpdate() || this.treatmentCreate()
  );

  readonly petEditorDisabled = computed(
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
        this.afterRemovePet(pet);
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
