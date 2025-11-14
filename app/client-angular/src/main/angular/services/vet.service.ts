import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { backendUrl } from "../app.routes";
import { type VetItem, type Vet } from "../types/vet.type";
import { tapLog } from "../utils/log";
import { map, Observable } from "rxjs";

@Injectable()
export class VetService {
  constructor(private httpClient: HttpClient) {}

  public loadAllVet(params: HttpParams | undefined = undefined) {
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.httpClient.get<{ content: Vet[] }>(path, { params }).pipe(
      tapLog("GET", path),
      map((body) => body.content)
    );
  }

  public loadAllVetItem(): Observable<VetItem[]> {
    const params = new HttpParams().set("sort", "name,asc");
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.httpClient.get<{ content: Vet[] }>(path, { params }).pipe(
      tapLog("GET", path),
      map((body) => body.content.map(mapVetToVetItem))
    );
  }

  public createVet(value: Vet) {
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.httpClient
      .post<Vet>(path, value)
      .pipe(tapLog("POST", path, value));
  }

  public updateVet(value: Vet) {
    const path = [backendUrl(), "api", "vet", value.id].join("/");
    const headers = { "Content-Type": "application/merge-patch+json" };
    return this.httpClient
      .patch<Vet>(path, value, { headers })
      .pipe(tapLog("PATCH", path, value));
  }

  public removeVet(id: string) {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.httpClient.delete<Vet>(path).pipe(tapLog("DELETE", path));
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
