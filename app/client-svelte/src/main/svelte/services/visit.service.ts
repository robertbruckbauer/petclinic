import { Observable } from "rxjs";
import type { Visit } from "../types/visit.type";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VisitService extends BaseService {
  public loadAllVisit(query: string = ""): Observable<Visit[]> {
    const path = [backendUrl(), "api", "visit" + query].join("/");
    return this.restApiGetAll(path);
  }

  public loadOneVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiGet(path);
  }

  public createVisit(visit: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit"].join("/");
    return this.restApiPost(path, visit);
  }

  public updateVisit(visit: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", visit.id].join("/");
    return this.restApiPut(path, visit);
  }

  public mutateVisit(id: string, patch: Partial<Visit>): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiPatch(path, patch);
  }

  public removeVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.restApiDelete(path);
  }
}
