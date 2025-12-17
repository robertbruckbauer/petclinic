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
import { VisitService } from "../../../services/visit.service";
import { type Visit } from "../../../types/visit.type";

@Component({
  selector: "app-visit-treatment",
  imports: [ReactiveFormsModule],
  templateUrl: "./visit-treatment.html",
  styles: ``,
})
export class VisitTreatmentComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private visitService = inject(VisitService);
  visible = model.required<boolean>();
  visit = input.required<Visit>();
  form = new FormGroup({
    date: new FormControl("", Validators.required),
  });

  ngOnInit() {
    this.form.patchValue({
      date: this.visit().date,
    });
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Visit>({ alias: "cancel" });
  onCancelClicked() {
    this.form.patchValue({
      date: this.visit().date,
    });
    this.form.markAsPristine();
    this.cancelEmitter.emit(this.visit());
    this.visible.set(false);
  }

  createEmitter = output<Visit>({ alias: "create" });
  updateEmitter = output<Visit>({ alias: "update" });
  onSubmitClicked() {
    const newVisit = {
      ...this.visit(),
      date: this.form.value.date!,
    };
    if (this.visit().id) {
      const subscription = this.visitService
        .mutateVisit(this.visit().id!, newVisit)
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
      const subscription = this.visitService.createVisit(newVisit).subscribe({
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
    }
  }
}
