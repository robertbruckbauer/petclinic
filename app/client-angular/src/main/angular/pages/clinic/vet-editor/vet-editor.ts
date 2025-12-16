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
import { VetService } from "../../../services/vet.service";
import { type EnumItem } from "../../../types/enum.type";
import { type Vet } from "../../../types/vet.type";

@Component({
  selector: "app-vet-editor",
  imports: [ReactiveFormsModule],
  templateUrl: "./vet-editor.html",
  styles: ``,
})
export class VetEditorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private vetService = inject(VetService);
  mode = input.required<"create" | "update">();
  visible = model.required<boolean>();
  allSkillEnum = input.required<EnumItem[]>();
  vet = input.required<Vet>();
  form = new FormGroup({
    name: new FormControl("", Validators.required),
    allSkill: new FormControl<string[]>([], Validators.required),
  });

  ngOnInit() {
    this.form.patchValue(this.vet());
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Vet>({ alias: "cancel" });
  onCancelClicked() {
    this.cancelEmitter.emit(this.vet());
    this.visible.set(false);
    this.form.reset();
  }

  createEmitter = output<Vet>({ alias: "create" });
  updateEmitter = output<Vet>({ alias: "update" });
  onSubmitClicked() {
    if (this.mode() === "create") {
      const subscription = this.vetService
        .createVet({
          ...this.vet(),
          name: this.form.value.name!,
          allSkill: this.form.value.allSkill!,
        })
        .subscribe({
          next: (value) => {
            this.createEmitter.emit(value);
            this.visible.set(false);
            this.form.reset();
          },
          error: (err) => {
            this.toast.push(err);
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.vetService
        .mutateVet(this.vet().id!, {
          ...this.vet(),
          name: this.form.value.name!,
          allSkill: this.form.value.allSkill!,
        })
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
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
