import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  model,
  output,
} from "@angular/core";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { PetService } from "../../../services/pet.service";
import { type EnumItem } from "../../../types/enum.type";
import { type Pet } from "../../../types/pet.type";

@Component({
  selector: "app-pet-editor",
  imports: [ReactiveFormsModule],
  templateUrl: "./pet-editor.html",
  styles: ``,
})
export class PetEditorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private petService = inject(PetService);
  mode = input.required<"create" | "update">();
  visible = model.required<boolean>();
  allSpeciesEnum = input.required<EnumItem[]>();
  pet = input.required<Pet>();
  form = new FormGroup({
    name: new FormControl("", Validators.required),
    born: new FormControl("", Validators.required),
    species: new FormControl("", Validators.required),
  });

  ngOnInit() {
    this.form.patchValue(this.pet());
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Pet>({ alias: "cancel" });
  onCancelClicked() {
    this.cancelEmitter.emit(this.pet());
    this.visible.set(false);
    this.form.reset();
  }

  createEmitter = output<Pet>({ alias: "create" });
  updateEmitter = output<Pet>({ alias: "update" });
  onSubmitClicked() {
    if (this.mode() === "create") {
      const subscription = this.petService
        .createPet({
          ...this.pet(),
          name: this.form.value.name!,
          born: this.form.value.born!,
          species: this.form.value.species!,
        })
        .subscribe({
          next: (value) => {
            this.createEmitter.emit(value);
            this.visible.set(false);
            this.form.reset();
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.petService
        .mutatePet(this.pet().id!, {
          ...this.pet(),
          name: this.form.value.name!,
          born: this.form.value.born!,
          species: this.form.value.species!,
        })
        .subscribe({
          next: (value) => {
            this.updateEmitter.emit(value);
            this.visible.set(false);
            this.form.reset();
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
