import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { backendUrl } from "../app.routes";
import { type OwnerItem, type Owner } from "../types/owner.type";
import { tapLog } from "../utils/log";
import { map, Observable } from "rxjs";

@Injectable()
export class OwnerService {
  private httpClient = inject(HttpClient);

  public loadAllOwner(params: HttpParams | undefined = undefined) {
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.httpClient.get<{ content: Owner[] }>(path, { params }).pipe(
      tapLog("GET", path),
      map((body) => body.content)
    );
  }

  public loadAllOwnerItem(): Observable<OwnerItem[]> {
    const params = new HttpParams().set("sort", "name,asc");
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.httpClient.get<{ content: Owner[] }>(path, { params }).pipe(
      tapLog("GET", path),
      map((body) =>
        body.content.map((e) => ({
          value: e.id!,
          text: e.name + ", " + e.address,
        }))
      )
    );
  }

  public createOwner(value: Owner) {
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.httpClient.post<Owner>(path, value).pipe(tapLog("POST", path));
  }

  public updateOwner(value: Owner) {
    const path = [backendUrl(), "api", "owner", value.id].join("/");
    return this.httpClient.put<Owner>(path, value).pipe(tapLog("PUT", path));
  }

  public removeOwner(id: string) {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.httpClient.delete<Owner>(path).pipe(tapLog("DELETE", path));
  }
}
