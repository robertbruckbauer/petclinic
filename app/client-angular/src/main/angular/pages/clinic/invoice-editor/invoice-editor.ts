import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  model,
  output,
  signal,
} from "@angular/core";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { Subject, debounceTime, distinctUntilChanged, switchMap } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Toast } from "../../../controls/toast/toast";
import { InvoiceService } from "../../../services/invoice.service";
import { VisitService } from "../../../services/visit.service";
import { type VisitItem } from "../../../types/visit.type";
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
  private visitService = inject(VisitService);
  visible = model.required<boolean>();
  invoice = input.required<Invoice>();
  form = new FormGroup({
    issued: new FormControl("", Validators.required),
    days: new FormControl(0, [Validators.required, Validators.min(0)]),
    status: new FormControl<InvoiceStatus>("D", Validators.required),
    text: new FormControl("", [Validators.required, Validators.minLength(1)]),
  });

  allVisitItem = signal<VisitItem[]>([]);
  selectedVisitId = signal<string | null>(null);
  visitKeyIndex = signal(-1);
  visitFilter = signal("");
  private visitFilter$ = new Subject<string>();

  private parseDays(iso: string): number {
    const match = /PT(\d+)H/.exec(iso ?? "");
    const hours = match?.[1] ? parseInt(match[1], 10) : 0;
    return Math.round(hours / 24);
  }

  private buildPeriod(days: number): string {
    if (days === 0) return "PT0S";
    return `PT${days * 24}H`;
  }

  ngOnInit() {
    const days = this.parseDays(this.invoice().period);
    this.form.patchValue({ ...this.invoice(), days });
    this.selectedVisitId.set(this.invoice().visit ?? null);

    this.visitFilter$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((text) => this.visitService.loadAllVisitByText(text)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (items) => {
          this.allVisitItem.set(items);
        },
        error: (err) => {
          this.toast.push(err);
        },
      });
  }

  onVisitFilterInput(event: Event) {
    const text = (event.target as HTMLInputElement).value;
    this.visitFilter.set(text);
    this.visitKeyIndex.set(-1);
    if (text.length >= 2) {
      this.visitFilter$.next(text);
    } else {
      this.allVisitItem.set([]);
    }
  }

  onVisitKeyDown(event: KeyboardEvent) {
    if (!this.allVisitItem().length) return;
    if (event.key === "ArrowDown") {
      event.preventDefault();
      this.visitKeyIndex.update((i) =>
        Math.min(i + 1, this.allVisitItem().length - 1)
      );
    } else if (event.key === "ArrowUp") {
      event.preventDefault();
      this.visitKeyIndex.update((i) => Math.max(i - 1, 0));
    } else if (event.key === "Enter") {
      event.preventDefault();
      const idx = this.visitKeyIndex();
      if (idx >= 0) this.onVisitSelected(this.allVisitItem()[idx]);
    } else if (event.key === "Escape" || event.key === "Tab") {
      this.allVisitItem.set([]);
      this.visitKeyIndex.set(-1);
    }
  }

  onVisitSelected(item: VisitItem) {
    this.selectedVisitId.set(item.value);
    this.visitFilter.set(item.text);
    this.allVisitItem.set([]);
    this.visitKeyIndex.set(-1);
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
      issued: this.form.value.issued!,
      period: this.buildPeriod(this.form.value.days ?? 0),
      status: this.form.value.status!,
      text: this.form.value.text!,
      visit: this.selectedVisitId()
        ? `/api/visit/${this.selectedVisitId()}`
        : this.invoice().visit,
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
