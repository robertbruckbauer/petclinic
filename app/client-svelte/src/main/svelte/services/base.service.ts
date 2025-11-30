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

  protected restApiGetAll<T>(path: string): Observable<T[]> {
    return from(
      fetch(path, {
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
        console.log([["GET", path].join(" "), resBody]);
      })
    );
  }

  protected restApiGet<T>(path: string): Observable<T> {
    return from(
      fetch(path, {
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
        console.log([["GET", path].join(" "), resBody]);
      })
    );
  }

  protected restApiPost<T>(path: string, reqBody: T): Observable<T> {
    return from(
      fetch(path, {
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
        console.log([["POST", path].join(" "), reqBody, resBody]);
      })
    );
  }

  protected restApiPut<T>(path: string, reqBody: T): Observable<T> {
    return from(
      fetch(path, {
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
        console.log([["PUT", path].join(" "), reqBody, resBody]);
      })
    );
  }

  protected restApiPatch<T>(path: string, reqBody: Partial<T>): Observable<T> {
    return from(
      fetch(path, {
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
        console.log([["PATCH", path].join(" "), reqBody, resBody]);
      })
    );
  }

  protected restApiDelete<T>(path: string): Observable<T> {
    return from(
      fetch(path, {
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
        console.log([["DELETE", path].join(" "), resBody]);
      })
    );
  }
}
