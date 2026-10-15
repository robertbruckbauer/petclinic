import { Observable } from "rxjs";
import type { Invoice } from "../types/invoice.type";
import { BackendService } from "./backend.service";

export class InvoiceService extends BackendService {
  public loadAllInvoice(
    search: Record<string, string> = {}
  ): Observable<Invoice[]> {
    const path = ["api", "invoice"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadOneInvoice(id: string): Observable<Invoice> {
    const path = ["api", "invoice", id].join("/");
    return this.restApiGet(path);
  }

  public createInvoice(invoice: Invoice): Observable<Invoice> {
    const path = ["api", "invoice"].join("/");
    return this.restApiPost(path, invoice);
  }

  public mutateInvoice(
    id: string,
    patch: Partial<Invoice>
  ): Observable<Invoice> {
    const path = ["api", "invoice", id].join("/");
    return this.restApiPatch(path, patch);
  }

  public removeInvoice(id: string): Observable<Invoice> {
    const path = ["api", "invoice", id].join("/");
    return this.restApiDelete(path);
  }
}
