import { map, Observable, from, switchMap, throwError } from "rxjs";
import { type Vet, type VetItem } from "../types/vet.type";
import { type ErrorItem } from "../types/error.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";

export class VetService {
  public loadAllVet(query: string = ""): Observable<Vet[]> {
    const path = [backendUrl(), "api", "vet" + query].join("/");
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
      map((body: { content: Vet[] }) => body.content)
    );
  }

  public loadOneVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
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

  public loadAllVetItem(): Observable<VetItem[]> {
    const path = [backendUrl(), "api", "vet", "item"].join("/");
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
      map((body: { content: VetItem[] }) => body.content)
    );
  }

  public createVet(vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(vet),
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
      tapLog("POST", path, vet)
    );
  }

  public updateVet(id: string, vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(vet),
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
      tapLog("PUT", path, vet)
    );
  }

  public removeVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
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

export function mapVetToVetItem(value: Vet): VetItem {
  return {
    value: value.id!,
    text: value.name,
  };
}
