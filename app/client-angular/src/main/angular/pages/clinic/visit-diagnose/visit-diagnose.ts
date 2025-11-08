import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  model,
  output,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors,
} from "@angular/forms";
import { VisitService } from "../../../services/visit.service";
import { compareVetItem } from "../../../services/vet.service";
import { type VetItem } from "../../../types/vet.type";
import { type Visit } from "../../../types/visit.type";

@Component({
  selector: "app-visit-diagnose",
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./visit-diagnose.html",
  styles: ``,
})
export class VisitDiagnoseComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private visitService = inject(VisitService);
  mode = input.required<"create" | "update">();
  visible = model.required<boolean>();
  allVetItem = input.required<VetItem[]>();
  visit = input.required<Visit>();
  form = new FormGroup({
    text: new FormControl("", Validators.required),
    vetItem: new FormControl<VetItem | null>(null, (control) => {
      const item = control.value as VetItem | null;
      if (!item || !item.value || item.value.trim() === "") {
        return { required: true };
      }
      return null;
    }),
  });

  readonly compareVetItem = compareVetItem;

  ngOnInit() {
    this.form.patchValue(this.visit());
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Visit>({ alias: "cancel" });
  onCancelClicked() {
    this.cancelEmitter.emit(this.visit());
    this.visible.set(false);
    this.form.reset();
  }

  createEmitter = output<Visit>({ alias: "create" });
  updateEmitter = output<Visit>({ alias: "update" });
  onSubmitClicked() {
    if (this.mode() === "create") {
      const subscription = this.visitService
        .createVisit({
          ...this.visit(),
          text: this.form.value.text!,
          vetItem: this.form.value.vetItem!,
          vet: "/api/vet/" + this.form.value.vetItem!.value,
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
      const subscription = this.visitService
        .updateVisit({
          ...this.visit(),
          text: this.form.value.text!,
          vetItem: this.form.value.vetItem!,
          vet: "/api/vet/" + this.form.value.vetItem!.value,
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
