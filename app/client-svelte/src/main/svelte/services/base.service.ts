import {
  Observable,
  catchError,
  from,
  map,
  switchMap,
  tap,
  throwError,
} from "rxjs";
import type { ErrorItem } from "../types/error.type";

export abstract class BaseService {
  handleResponse<T>(res: Response): Observable<T> {
    if (res.ok) {
      return from(res.json());
    }
    return from(res.json()).pipe(
      switchMap((err: ErrorItem) => throwError(() => err))
    );
  }

  handleError(path: string, cause: any): Observable<Response> {
    let error: ErrorItem = {
      instance: path,
      status: 500,
      detail: cause.toString(),
    };
    return throwError(() => error);
  }

  protected restApiGetAll<T>(
    path: string,
    search: Record<string, string>
  ): Observable<T[]> {
    const url = new URL(path);
    url.search = new URLSearchParams(search).toString();
    return from(
      fetch(url.toString(), {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      catchError((err) => this.handleError(path, err)),
      switchMap((res) => this.handleResponse<{ content: T[] }>(res)),
      map((resBody: { content: T[] }) => resBody.content),
      tap((resBody) => {
        console.log([["GET", url].join(" "), resBody]);
      })
    );
  }

  protected restApiGet<T>(path: string): Observable<T> {
    const url = new URL(path);
    return from(
      fetch(url.toString(), {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      catchError((err) => this.handleError(path, err)),
      switchMap((res) => this.handleResponse<T>(res)),
      tap((resBody) => {
        console.log([["GET", url].join(" "), resBody]);
      })
    );
  }

  protected restApiPost<T>(path: string, reqBody: T): Observable<T> {
    const url = new URL(path);
    return from(
      fetch(url.toString(), {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(reqBody),
      })
    ).pipe(
      catchError((err) => this.handleError(path, err)),
      switchMap((res) => this.handleResponse<T>(res)),
      tap((resBody) => {
        console.log([["POST", url].join(" "), reqBody, resBody]);
      })
    );
  }

  protected restApiPut<T>(path: string, reqBody: T): Observable<T> {
    const url = new URL(path);
    return from(
      fetch(url.toString(), {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(reqBody),
      })
    ).pipe(
      catchError((err) => this.handleError(path, err)),
      switchMap((res) => this.handleResponse<T>(res)),
      tap((resBody) => {
        console.log([["PUT", url].join(" "), reqBody, resBody]);
      })
    );
  }

  protected restApiPatch<T>(path: string, reqBody: Partial<T>): Observable<T> {
    const url = new URL(path);
    return from(
      fetch(url.toString(), {
        mode: "cors",
        method: "PATCH",
        headers: {
          Accept: "application/json",
          "Content-type": "application/merge-patch+json",
        },
        body: JSON.stringify(reqBody),
      })
    ).pipe(
      catchError((err) => this.handleError(path, err)),
      switchMap((res) => this.handleResponse<T>(res)),
      tap((resBody) => {
        console.log([["PATCH", url].join(" "), reqBody, resBody]);
      })
    );
  }

  protected restApiDelete<T>(path: string): Observable<T> {
    const url = new URL(path);
    return from(
      fetch(url.toString(), {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      catchError((err) => this.handleError(path, err)),
      switchMap((res) => this.handleResponse<T>(res)),
      tap((resBody) => {
        console.log([["DELETE", url].join(" "), resBody]);
      })
    );
  }
}
