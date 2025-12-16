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
} from "@angular/forms";
import { Toast } from "../../../controls/toast/toast";
import { VisitService } from "../../../services/visit.service";
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
  private toast = inject(Toast);
  private visitService = inject(VisitService);
  mode = input.required<"create" | "update">();
  visible = model.required<boolean>();
  allVetItem = input.required<VetItem[]>();
  visit = input.required<Visit>();
  form = new FormGroup({
    text: new FormControl("", Validators.required),
    vet: new FormControl("", Validators.required),
  });

  ngOnInit() {
    this.form.patchValue({
      text: this.visit().text,
      vet: this.visit().vetItem?.value || "",
    });
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Visit>({ alias: "cancel" });
  onCancelClicked() {
    this.form.patchValue({
      text: this.visit().text,
      vet: this.visit().vetItem?.value || "",
    });
    this.form.markAsPristine();
    this.cancelEmitter.emit(this.visit());
    this.visible.set(false);
  }

  createEmitter = output<Visit>({ alias: "create" });
  updateEmitter = output<Visit>({ alias: "update" });
  onSubmitClicked() {
    if (this.mode() === "create") {
      const subscription = this.visitService
        .createVisit({
          ...this.visit(),
          text: this.form.value.text!,
          vetItem: undefined, // vetItem is invalid
          vet: "/api/vet/" + this.form.value.vet!,
        })
        .subscribe({
          next: (value) => {
            this.createEmitter.emit(value);
            this.visible.set(false);
          },
          error: (err) => {
            this.toast.push(err);
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.visitService
        .mutateVisit(this.visit().id!, {
          ...this.visit(),
          text: this.form.value.text!,
          vetItem: undefined, // vetItem is invalid
          vet: "/api/vet/" + this.form.value.vet!,
        })
        .subscribe({
          next: (value) => {
            this.updateEmitter.emit(value);
            this.visible.set(false);
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
