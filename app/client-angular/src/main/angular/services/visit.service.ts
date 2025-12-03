import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, map, Observable, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type Visit } from "../types/visit.type";
import { BaseService } from "./base.service";

@Injectable()
export class VisitService extends BaseService {
  constructor(private httpClient: HttpClient) {
    super();
  }

  public loadAllVisit(
    search: Record<string, string> = {}
  ): Observable<Visit[]> {
    const path = [backendUrl(), "api", "visit"].join("/");
    const params = new HttpParams({ fromObject: search });
    return this.httpClient.get<{ content: Visit[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public loadVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.httpClient.get<Visit>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public createVisit(value: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit"].join("/");
    return this.httpClient.post<Visit>(path, value).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["POST", path].join(" "), value, body]);
      })
    );
  }

  public updateVisit(value: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", value.id].join("/");
    return this.httpClient.put<Visit>(path, value).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["PUT", path].join(" "), value, body]);
      })
    );
  }

  public mutateVisit(id: string, value: Visit): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    const headers = { "Content-Type": "application/merge-patch+json" };
    return this.httpClient.patch<Visit>(path, value, { headers }).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["PATCH", path].join(" "), value, body]);
      })
    );
  }

  public removeVisit(id: string): Observable<Visit> {
    const path = [backendUrl(), "api", "visit", id].join("/");
    return this.httpClient.delete<Visit>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["DELETE", path].join(" "), body]);
      })
    );
  }
}
