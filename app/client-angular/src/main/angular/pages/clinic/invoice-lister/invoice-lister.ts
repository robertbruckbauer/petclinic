import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  effect,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { Toast } from "../../../controls/toast/toast";
import { InvoiceService } from "../../../services/invoice.service";
import { type Invoice } from "../../../types/invoice.type";
import { InvoiceEditorComponent } from "../invoice-editor/invoice-editor";

@Component({
  selector: "app-invoice-lister",
  imports: [CommonModule, ReactiveFormsModule, InvoiceEditorComponent],
  templateUrl: "./invoice-lister.html",
  styles: ``,
})
export class InvoiceListerComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private invoiceService = inject(InvoiceService);
  loading = signal(false);

  filterForm = new FormGroup({
    criteria: new FormControl("", Validators.required),
  });

  allInvoice = signal<Invoice[]>([]);
  afterCreateInvoice(newInvoice: Invoice) {
    this.allInvoice.update((allInvoice) => {
      return [newInvoice, ...allInvoice];
    });
  }
  afterUpdateInvoice(newInvoice: Invoice) {
    this.allInvoice.update((allInvoice) => {
      return allInvoice.map((invoice) =>
        invoice.id === newInvoice.id ? newInvoice : invoice
      );
    });
  }
  afterRemoveInvoice(newInvoice: Invoice) {
    this.allInvoice.update((allInvoice) => {
      return allInvoice.filter((invoice) => invoice.id !== newInvoice.id);
    });
  }

  newInvoice = computed<Invoice>(() => {
    const today = new Date().toISOString().substring(0, 10);
    return {
      version: 0,
      at: today,
      due: today,
      status: "D",
      text: "",
    };
  });

  ngOnInit() {
    // No extra data to load
  }

  constructor() {
    effect(() => this.onFilterClicked());
  }

  /**
   * Start swimlane if the due date changes
   * in a due-date ordered array.
   */
  isSwimlaneChange(index: number) {
    if (index) {
      const invoice1 = this.allInvoice()[index - 1];
      const invoice2 = this.allInvoice()[index];
      return invoice1.due !== invoice2.due;
    } else {
      return true;
    }
  }

  onFilterClicked() {
    this.loading.set(true);
    const search = {
      sort: "due,desc",
      ...(this.filterForm.value.criteria
        ? { text: "%" + this.filterForm.value.criteria + "%" }
        : {}),
    };
    const subscription = this.invoiceService.loadAllInvoice(search).subscribe({
      next: (allInvoice) => {
        this.allInvoice.set(allInvoice);
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

  invoiceId = signal<string | undefined>(undefined); // no invoice selected
  onInvoiceClicked(invoice: Invoice) {
    this.invoiceId.set(invoice.id);
  }

  invoiceEditorCreate = signal(false);
  onInvoiceEditorCreateClicked() {
    this.invoiceId.set(undefined); // no invoice selected
    this.invoiceEditorCreate.set(true);
    this.invoiceEditorUpdate.set(false);
  }

  invoiceEditorUpdate = signal(false);
  onInvoiceEditorUpdateClicked(invoice: Invoice) {
    this.invoiceId.set(invoice.id);
    this.invoiceEditorCreate.set(false);
    this.invoiceEditorUpdate.set(true);
  }

  readonly invoiceFilterDisabled = computed(
    () => this.invoiceEditorCreate() || this.invoiceEditorUpdate()
  );

  readonly invoiceEditorDisabled = computed(() => this.invoiceFilterDisabled());

  onInvoiceRemoveClicked(invoice: Invoice) {
    this.invoiceId.set(undefined); // no invoice selected
    const hint = invoice.at;
    if (!confirm("Delete invoice '" + hint + "' permanently?")) return;
    this.loading.set(true);
    const subscription = this.invoiceService
      .removeInvoice(invoice.id!)
      .subscribe({
        next: (invoice) => {
          this.afterRemoveInvoice(invoice);
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
}
