import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { backendUrl } from "../app.routes";
import { type Owner } from "../types/owner.type";
import { tapLog } from "../utils/log";
import { map } from "rxjs";

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
