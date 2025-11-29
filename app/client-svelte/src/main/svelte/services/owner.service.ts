import { map, Observable, from, switchMap, throwError } from "rxjs";
import type { ErrorItem } from "../types/error.type";
import type { Owner, OwnerItem } from "../types/owner.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";

export class OwnerService {
  public loadAllOwner(query: string = ""): Observable<Owner[]> {
    const path = [backendUrl(), "api", "owner" + query].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("GET", path),
      map((body: { content: Owner[] }) => body.content)
    );
  }

  public loadOneOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("GET", path)
    );
  }

  public loadAllOwnerItem(): Observable<OwnerItem[]> {
    const path = [backendUrl(), "api", "owner", "item"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("GET", path),
      map((body: { content: OwnerItem[] }) => body.content)
    );
  }

  public createOwner(owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(owner),
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("POST", path, owner)
    );
  }

  public updateOwner(id: string, owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(owner),
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("PUT", path, owner)
    );
  }

  public removeOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("DELETE", path)
    );
  }
}

export function mapOwnerToOwnerItem(value: Owner): OwnerItem {
  return {
    value: value.id!,
    text: value.name + ", " + value.address,
  };
}
