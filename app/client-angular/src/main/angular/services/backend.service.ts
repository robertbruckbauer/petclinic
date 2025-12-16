import {
  HttpClient,
  HttpParams,
  HttpErrorResponse,
} from "@angular/common/http";
import { catchError, map, Observable, tap, throwError } from "rxjs";
import type { ErrorItem } from "../types/error.type";

export abstract class BackendService {
  constructor(private httpClient: HttpClient) {}

  private handleError(url: URL) {
    return (err: HttpErrorResponse) => {
      if (err.status === 0) {
        const error: ErrorItem = {
          instance: url.pathname,
          status: 0,
          title: "Network Error",
          detail: err.message,
        };
        return throwError(() => error);
      }
      return throwError(() => err.error as ErrorItem);
    };
  }

  protected backendUrl() {
    return (
      window.location.protocol +
      "//" +
      window.location.host.replace("5052", "8080")
    );
  }

  // tag::restApiGetAll[]
  protected restApiGetAll<T>(
    path: string,
    search: Record<string, string>
  ): Observable<T[]> {
    const url = new URL(path, this.backendUrl());
    const params = new HttpParams({ fromObject: search });
    return this.httpClient
      .get<{ content: T[] }>(url.toString(), { params })
      .pipe(
        catchError(this.handleError(url)),
        map((resBody) => resBody.content),
        tap((resBody) => {
          console.log([["GET", url].join(" "), resBody]);
        })
      );
  }
  // end::restApiGetAll[]

  // tag::restApiGet[]
  protected restApiGet<T>(path: string): Observable<T> {
    const url = new URL(path, this.backendUrl());
    return this.httpClient.get<T>(url.toString()).pipe(
      catchError(this.handleError(url)),
      tap((body) => {
        console.log([["GET", url].join(" "), body]);
      })
    );
  }
  // end::restApiGet[]

  // tag::restApiPost[]
  protected restApiPost<T>(path: string, reqBody: T): Observable<T> {
    const url = new URL(path, this.backendUrl());
    const headers = { "Content-Type": "application/json" };
    return this.httpClient.post<T>(url.toString(), reqBody, { headers }).pipe(
      catchError(this.handleError(url)),
      tap((resBody) => {
        console.log([["POST", url].join(" "), reqBody, resBody]);
      })
    );
  }
  // end::restApiPost[]

  // tag::restApiPut[]
  protected restApiPut<T>(path: string, reqBody: T): Observable<T> {
    const url = new URL(path, this.backendUrl());
    const headers = { "Content-Type": "application/json" };
    return this.httpClient.put<T>(url.toString(), reqBody, { headers }).pipe(
      catchError(this.handleError(url)),
      tap((resBody) => {
        console.log([["PUT", url].join(" "), reqBody, resBody]);
      })
    );
  }
  // end::restApiPut[]

  // tag::restApiPatch[]
  protected restApiPatch<T>(path: string, reqBody: Partial<T>): Observable<T> {
    const url = new URL(path, this.backendUrl());
    const headers = { "Content-Type": "application/merge-patch+json" };
    return this.httpClient.patch<T>(url.toString(), reqBody, { headers }).pipe(
      catchError(this.handleError(url)),
      tap((resBody) => {
        console.log([["PATCH", url].join(" "), reqBody, resBody]);
      })
    );
  }
  // end::restApiPatch[]

  // tag::restApiDelete[]
  protected restApiDelete<T>(path: string): Observable<T> {
    const url = new URL(path, this.backendUrl());
    return this.httpClient.delete<T>(url.toString()).pipe(
      catchError(this.handleError(url)),
      tap((body) => {
        console.log([["DELETE", url].join(" "), body]);
      })
    );
  }
  // end::restApiDelete[]
}
