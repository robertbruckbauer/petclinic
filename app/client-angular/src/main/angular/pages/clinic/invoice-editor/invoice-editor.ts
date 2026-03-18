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
import { InvoiceService } from "../../../services/invoice.service";
import { type Invoice, type InvoiceStatus } from "../../../types/invoice.type";

@Component({
  selector: "app-invoice-editor",
  imports: [ReactiveFormsModule],
  templateUrl: "./invoice-editor.html",
  styles: ``,
})
export class InvoiceEditorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private invoiceService = inject(InvoiceService);
  visible = model.required<boolean>();
  invoice = input.required<Invoice>();
  form = new FormGroup({
    at: new FormControl("", Validators.required),
    due: new FormControl("", Validators.required),
    status: new FormControl<InvoiceStatus>("D", Validators.required),
    text: new FormControl("", [Validators.required, Validators.minLength(1)]),
  });

  ngOnInit() {
    this.form.patchValue(this.invoice());
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<Invoice>({ alias: "cancel" });
  onCancelClicked() {
    this.cancelEmitter.emit(this.invoice());
    this.visible.set(false);
    this.form.reset();
  }

  createEmitter = output<Invoice>({ alias: "create" });
  updateEmitter = output<Invoice>({ alias: "update" });
  onSubmitClicked() {
    const newInvoice: Invoice = {
      ...this.invoice(),
      at: this.form.value.at!,
      due: this.form.value.due!,
      status: this.form.value.status!,
      text: this.form.value.text!,
    };
    if (this.invoice().id) {
      const subscription = this.invoiceService
        .mutateInvoice(this.invoice().id!, newInvoice)
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
    } else {
      const subscription = this.invoiceService
        .createInvoice(newInvoice)
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
    }
  }
}
