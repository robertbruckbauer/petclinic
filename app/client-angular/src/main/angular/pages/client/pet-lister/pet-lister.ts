import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  effect,
  inject,
  signal,
} from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { RouterLink } from "@angular/router";
import { forkJoin } from "rxjs";
import { Toast } from "../../../controls/toast/toast";
import { EnumService } from "../../../services/enum.service";
import { OwnerService } from "../../../services/owner.service";
import { PetService } from "../../../services/pet.service";
import { getStoredOwner, setStoredOwner } from "../../../stores/owner.store";
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
    RouterLink,
    ReactiveFormsModule,
    PetEditorComponent,
    VisitTreatmentComponent,
  ],
  templateUrl: "./pet-lister.html",
  styles: ``,
})
export class PetListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private enumService = inject(EnumService);
  private ownerService = inject(OwnerService);
  private petService = inject(PetService);
  loading = signal(false);

  filterForm = new FormGroup({
    ownerId: new FormControl(getStoredOwner().id || "", Validators.required),
  });
  filterFormValue = toSignal(this.filterForm.valueChanges, {
    initialValue: this.filterForm.value,
  });
  ownerId = computed(() => this.filterFormValue().ownerId!);

  allPet = signal<Pet[]>([]);
  // tag::afterCreate[]
  afterCreatePet(newPet: Pet) {
    this.allPet.update((allPet) => {
      return [newPet, ...allPet];
    });
  }
  // end::afterCreate[]
  // tag::afterUpdate[]
  afterUpdatePet(newPet: Pet) {
    this.allPet.update((allPet) => {
      return allPet.map((pet) => (pet.id === newPet.id ? newPet : pet));
    });
  }
  // end::afterUpdate[]
  // tag::afterRemove[]
  afterRemovePet(newPet: Pet) {
    this.allPet.update((allPet) => {
      return allPet.filter((pet) => pet.id !== newPet.id);
    });
  }
  // end::afterRemove[]

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
      error: (err) => {
        this.toast.push(err);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }

  constructor() {
    effect(() => this.onFilterClicked());
  }

  onFilterClicked() {
    this.loading.set(true);
    // tag::loadAll[]
    const search = { sort: "name,asc", "owner.id": this.ownerId() };
    const subscription = this.petService.loadAllPet(search).subscribe({
      next: (allPet) => {
        this.allPet.set(allPet);
        // make that owner persistent
        setStoredOwner({ id: this.ownerId() });
      },
      complete: () => {
        this.loading.set(false);
      },
      error: (err) => {
        this.toast.push(err);
      },
    });
    // end::loadAll[]
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

  // tag::remove[]
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
      error: (err) => {
        this.toast.push(err);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
  // end::remove[]
}
