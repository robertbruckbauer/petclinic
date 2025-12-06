import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map } from "rxjs";
import { type VetItem, type Vet } from "../types/vet.type";
import { BackendService } from "./backend.service";

@Injectable()
export class VetService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  public loadAllVet(search: Record<string, string> = {}): Observable<Vet[]> {
    const path = ["api", "vet"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadAllVetItem(): Observable<VetItem[]> {
    const path = ["api", "vet", "search", "findAllItem"].join("/");
    return this.restApiGetAll(path, {});
  }

  public loadOneVet(id: string): Observable<Vet> {
    const path = ["api", "vet", id].join("/");
    return this.restApiGet(path);
  }

  public createVet(value: Vet): Observable<Vet> {
    const path = ["api", "vet"].join("/");
    return this.restApiPost(path, value);
  }

  public mutateVet(id: string, value: Partial<Vet>): Observable<Vet> {
    const path = ["api", "vet", value.id].join("/");
    return this.restApiPatch(path, value);
  }

  public removeVet(id: string): Observable<Vet> {
    const path = ["api", "vet", id].join("/");
    return this.restApiDelete(path);
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
