import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { backendUrl } from "../app.routes";
import { type Visit } from "../types/visit.type";
import { tapLog } from "../utils/log";
import { map } from "rxjs";

@Injectable()
export class VisitService {
  private httpClient = inject(HttpClient);

  public loadAllVisit(params: HttpParams | undefined = undefined) {
    const path = [backendUrl(), "api", "visit"].join("/");
    return this.httpClient.get<{ content: Visit[] }>(path, { params }).pipe(
      tapLog("GET", path),
      map((body) => body.content)
    );
  }

  public createVisit(value: Visit) {
    const path = [backendUrl(), "api", "visit"].join("/");
    return this.httpClient
      .post<Visit>(path, value)
      .pipe(tapLog("POST", path, value));
  }

  public updateVisit(value: Visit) {
    const path = [backendUrl(), "api", "visit", value.id].join("/");
    return this.httpClient
      .put<Visit>(path, value)
      .pipe(tapLog("PUT", path, value));
  }

  public removeVisit(id: string) {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.httpClient.delete<Visit>(path).pipe(tapLog("DELETE", path));
  }
}
