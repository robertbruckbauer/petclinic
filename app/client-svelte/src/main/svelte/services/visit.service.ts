import { map, Observable, from, switchMap, throwError } from "rxjs";
import { type Visit } from "../types/visit.type";
import { type ErrorItem } from "../types/error.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";

export class VisitService {
  public loadAllVisit(query: string = ""): Observable<Visit[]> {
    const path = [backendUrl(), "api", "visit" + query].join("/");
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
      map((body: { content: Visit[] }) => body.content)
    );
  }

  public loadOneVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
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

  public createVisit(visit: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(visit),
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
      tapLog("POST", path, visit)
    );
  }

  public updateVisit(id: string, visit: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(visit),
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
      tapLog("PUT", path, visit)
    );
  }

  public updateVisitPatch(
    id: string,
    patch: Partial<Visit>
  ): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PATCH",
        headers: {
          Accept: "application/json",
          "Content-type": "application/merge-patch+json",
        },
        body: JSON.stringify(patch),
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
      tapLog("PATCH", path, patch)
    );
  }

  public removeVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
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
