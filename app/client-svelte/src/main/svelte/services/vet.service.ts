import { map, Observable, from, switchMap } from "rxjs";
import type { Vet, VetItem } from "../types/vet.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VetService extends BaseService {
  public loadAllVet(query: string = ""): Observable<Vet[]> {
    const path = [backendUrl(), "api", "vet" + query].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<{ content: Vet[] }>),
      tapLog("GET", path),
      map((body: { content: Vet[] }) => body.content)
    );
  }

  public loadAllVetItem(): Observable<VetItem[]> {
    const path = [backendUrl(), "api", "vet", "item"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<{ content: VetItem[] }>),
      tapLog("GET", path),
      map((body: { content: VetItem[] }) => body.content)
    );
  }

  public loadOneVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Vet>),
      tapLog("GET", path)
    );
  }

  public createVet(vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(vet),
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Vet>),
      tapLog("POST", path, vet)
    );
  }

  public updateVet(id: string, vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(vet),
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Vet>),
      tapLog("PUT", path, vet)
    );
  }

  public removeVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Vet>),
      tapLog("DELETE", path)
    );
  }
}

export function mapVetToVetItem(value: Vet): VetItem {
  return {
    value: value.id!,
    text: value.name,
  };
}
