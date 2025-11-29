import { map, Observable, from, switchMap } from "rxjs";
import type { Visit } from "../types/visit.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VisitService extends BaseService {
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
      switchMap(this.mapResponseToObservableJson<{ content: Visit[] }>),
      tapLog("GET", path),
      map((body: { content: Visit[] }) => body.content)
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
      switchMap(this.mapResponseToObservableJson<Visit>),
      tapLog("POST", path, visit)
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
      switchMap(this.mapResponseToObservableJson<Visit>),
      tapLog("GET", path)
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
      switchMap(this.mapResponseToObservableJson<Visit>),
      tapLog("PUT", path, visit)
    );
  }

  public patchVisit(id: string, patch: Partial<Visit>): Observable<Visit> {
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
      switchMap(this.mapResponseToObservableJson<Visit>),
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
      switchMap(this.mapResponseToObservableJson<Visit>),
      tapLog("DELETE", path)
    );
  }
}
