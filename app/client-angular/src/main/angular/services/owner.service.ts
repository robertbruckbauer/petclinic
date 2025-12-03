import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { map, Observable, catchError, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type OwnerItem, type Owner } from "../types/owner.type";
import { BaseService } from "./base.service";

@Injectable()
export class OwnerService extends BaseService {
  constructor(private httpClient: HttpClient) {
    super();
  }

  public loadAllOwner(
    search: Record<string, string> = {}
  ): Observable<Owner[]> {
    const path = [backendUrl(), "api", "owner"].join("/");
    const params = new HttpParams({ fromObject: search });
    return this.httpClient.get<{ content: Owner[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public loadAllOwnerItem(): Observable<OwnerItem[]> {
    const params = new HttpParams().set("sort", "name,asc");
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.httpClient.get<{ content: Owner[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content.map(mapOwnerToOwnerItem)),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public createOwner(value: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.httpClient.post<Owner>(path, value).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["POST", path].join(" "), value, body]);
      })
    );
  }

  public updateOwner(value: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", value.id].join("/");
    const headers = { "Content-Type": "application/merge-patch+json" };
    return this.httpClient.patch<Owner>(path, value, { headers }).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["PATCH", path].join(" "), value, body]);
      })
    );
  }

  public removeOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.httpClient.delete<Owner>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["DELETE", path].join(" "), body]);
      })
    );
  }
}

export function mapOwnerToOwnerItem(value: Owner): OwnerItem {
  return {
    value: value.id!,
    text: value.name + ", " + value.address,
  };
}

export function compareOwnerItem(
  item1: OwnerItem | null,
  item2: OwnerItem | null
): boolean {
  return item1?.value === item2?.value;
}
