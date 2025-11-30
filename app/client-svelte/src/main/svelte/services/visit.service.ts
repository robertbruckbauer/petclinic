import { Observable, map, switchMap } from "rxjs";
import type { Visit } from "../types/visit.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VisitService extends BaseService {
  public loadAllVisit(query: string = ""): Observable<Visit[]> {
    const path = [backendUrl(), "api", "visit" + query].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: Visit[] }>),
      map((body: { content: Visit[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public createVisit(visit: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit"].join("/");
    return this.restApiPost(path, visit).pipe(
      switchMap(this.mapResponseToObservable<Visit>),
      tapLog("POST", path, visit)
    );
  }

  public loadOneVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<Visit>),
      tapLog("GET", path)
    );
  }

  public updateVisit(id: string, visit: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiPut(path, visit).pipe(
      switchMap(this.mapResponseToObservable<Visit>),
      tapLog("PUT", path, visit)
    );
  }

  public patchVisit(id: string, patch: Partial<Visit>): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiPatch(path, patch).pipe(
      switchMap(this.mapResponseToObservable<Visit>),
      tapLog("PATCH", path, patch)
    );
  }

  public removeVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiDelete(path).pipe(
      switchMap(this.mapResponseToObservable<Visit>),
      tapLog("DELETE", path)
    );
  }
}
