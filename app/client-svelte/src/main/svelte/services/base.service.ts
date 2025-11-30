import { Observable, from, switchMap, throwError } from "rxjs";
import type { ErrorItem } from "../types/error.type";

export abstract class BaseService {
  protected mapResponseToObservable<T>(res: Response): Observable<T> {
    if (res.ok) {
      return from(res.json());
    }
    return from(res.json()).pipe(
      switchMap((err: ErrorItem) => throwError(() => err))
    );
  }

  protected restApiGet(
    path: string,
    accept: string = "application/json"
  ): Observable<Response> {
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: accept,
        },
      })
    );
  }

  protected restApiPost<T>(path: string, body: T): Observable<Response> {
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(body),
      })
    );
  }

  protected restApiPut<T>(path: string, body: T): Observable<Response> {
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(body),
      })
    );
  }

  protected restApiPatch<T>(
    path: string,
    body: Partial<T>
  ): Observable<Response> {
    return from(
      fetch(path, {
        mode: "cors",
        method: "PATCH",
        headers: {
          Accept: "application/json",
          "Content-type": "application/merge-patch+json",
        },
        body: JSON.stringify(body),
      })
    );
  }

  protected restApiDelete(path: string): Observable<Response> {
    return from(
      fetch(path, {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    );
  }
}
