import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, map, Observable, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type VetItem, type Vet } from "../types/vet.type";
import { BaseService } from "./base.service";

@Injectable()
export class VetService extends BaseService {
  constructor(private httpClient: HttpClient) {
    super();
  }

  public loadAllVet(search: Record<string, string> = {}): Observable<Vet[]> {
    const path = [backendUrl(), "api", "vet"].join("/");
    const params = new HttpParams({ fromObject: search });
    return this.httpClient.get<{ content: Vet[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public loadAllVetItem(): Observable<VetItem[]> {
    const params = new HttpParams().set("sort", "name,asc");
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.httpClient.get<{ content: Vet[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content.map(mapVetToVetItem)),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public createVet(value: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.httpClient.post<Vet>(path, value).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["GET", path].join(" "), value, body]);
      })
    );
  }

  public updateVet(value: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", value.id].join("/");
    const headers = { "Content-Type": "application/merge-patch+json" };
    return this.httpClient.patch<Vet>(path, value, { headers }).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["PATCH", path].join(" "), value, body]);
      })
    );
  }

  public removeVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.httpClient.delete<Vet>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["DELETE", path].join(" "), body]);
      })
    );
  }
}

export function mapVetToVetItem(value: Vet): VetItem {
  return {
    value: value.id!,
    text: value.name,
  };
}

export function compareVetItem(
  item1: VetItem | null,
  item2: VetItem | null
): boolean {
  return item1?.value === item2?.value;
}
