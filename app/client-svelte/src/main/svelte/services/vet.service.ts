import { Observable, map, switchMap } from "rxjs";
import type { Vet, VetItem } from "../types/vet.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VetService extends BaseService {
  public loadAllVet(query: string = ""): Observable<Vet[]> {
    const path = [backendUrl(), "api", "vet" + query].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: Vet[] }>),
      map((body: { content: Vet[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public loadAllVetItem(): Observable<VetItem[]> {
    const path = [backendUrl(), "api", "vet", "item"].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: VetItem[] }>),
      map((body: { content: VetItem[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public loadOneVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<Vet>),
      tapLog("GET", path)
    );
  }

  public createVet(vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.restApiPost(path, vet).pipe(
      switchMap(this.mapResponseToObservable<Vet>),
      tapLog("POST", path, vet)
    );
  }

  public updateVet(id: string, vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.restApiPut(path, vet).pipe(
      switchMap(this.mapResponseToObservable<Vet>),
      tapLog("PUT", path, vet)
    );
  }

  public removeVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.restApiDelete(path).pipe(
      switchMap(this.mapResponseToObservable<Vet>),
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
