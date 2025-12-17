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
import { Toast } from "../../../controls/toast/toast";
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
  private toast = inject(Toast);
  private petService = inject(PetService);
  visible = model.required<boolean>();
  allSpeciesEnum = input.required<EnumItem[]>();
  pet = input.required<Pet>();
  form = new FormGroup({
    name: new FormControl("", Validators.required),
    born: new FormControl("", Validators.required),
    species: new FormControl("", Validators.required),
  });

  ngOnInit() {
    // tag::form[]
    this.form.patchValue(this.pet());
    // end::form[]
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Pet>({ alias: "cancel" });
  onCancelClicked() {
    // tag::cancel[]
    this.cancelEmitter.emit(this.pet());
    this.visible.set(false);
    this.form.reset();
    // end::cancel[]
  }

  createEmitter = output<Pet>({ alias: "create" });
  updateEmitter = output<Pet>({ alias: "update" });
  onSubmitClicked() {
    // tag::init[]
    const newPet = {
      ...this.pet(),
      name: this.form.value.name!,
      born: this.form.value.born!,
      species: this.form.value.species!,
    };
    // end::init[]
    if (this.pet().id) {
      // tag::update[]
      const subscription = this.petService
        .mutatePet(this.pet().id!, newPet)
        .subscribe({
          next: (value) => {
            this.updateEmitter.emit(value);
            this.visible.set(false);
            this.form.reset();
          },
          error: (err) => {
            this.toast.push(err);
          },
        });
      // end::update[]
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      // tag::create[]
      const subscription = this.petService.createPet(newPet).subscribe({
        next: (value) => {
          this.createEmitter.emit(value);
          this.visible.set(false);
          this.form.reset();
        },
        error: (err) => {
          this.toast.push(err);
        },
      });
      // end::create[]
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
