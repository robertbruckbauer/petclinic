import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { type Invoice } from "../types/invoice.type";
import { BackendService } from "./backend.service";

@Injectable()
export class InvoiceService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

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

  public createInvoice(value: Invoice): Observable<Invoice> {
    const path = ["api", "invoice"].join("/");
    return this.restApiPost(path, value);
  }

  public mutateInvoice(
    id: string,
    value: Partial<Invoice>
  ): Observable<Invoice> {
    const path = ["api", "invoice", id].join("/");
    return this.restApiPatch(path, value);
  }

  public removeInvoice(id: string): Observable<Invoice> {
    const path = ["api", "invoice", id].join("/");
    return this.restApiDelete(path);
  }
}
