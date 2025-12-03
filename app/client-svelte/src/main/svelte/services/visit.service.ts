import { Observable } from "rxjs";
import type { Visit } from "../types/visit.type";
import { BackendService } from "./backend.service";

export class VisitService extends BackendService {
  public loadAllVisit(
    search: Record<string, string> = {}
  ): Observable<Visit[]> {
    const path = ["api", "visit"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadOneVisit(id: string): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiGet(path);
  }

  public createVisit(visit: Visit): Observable<Visit> {
    const path = ["api", "visit"].join("/");
    return this.restApiPost(path, visit);
  }

  public updateVisit(visit: Visit): Observable<Visit> {
    const path = ["api", "visit", visit.id].join("/");
    return this.restApiPut(path, visit);
  }

  public mutateVisit(id: string, patch: Partial<Visit>): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiPatch(path, patch);
  }

  public removeVisit(id: string): Observable<Visit> {
    const path = ["api", "visit", id].join("/");
    return this.restApiDelete(path);
  }
}
