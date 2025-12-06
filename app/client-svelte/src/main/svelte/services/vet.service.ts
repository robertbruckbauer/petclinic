import { Observable } from "rxjs";
import type { Vet, VetItem } from "../types/vet.type";
import { BackendService } from "./backend.service";

export class VetService extends BackendService {
  public loadAllVet(search: Record<string, string> = {}): Observable<Vet[]> {
    const path = ["api", "vet"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadAllVetItem(
    search: Record<string, string> = {}
  ): Observable<VetItem[]> {
    const path = ["api", "vet", "item"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadOneVet(id: string): Observable<Vet> {
    const path = ["api", "vet", id].join("/");
    return this.restApiGet(path);
  }

  public createVet(vet: Vet): Observable<Vet> {
    const path = ["api", "vet"].join("/");
    return this.restApiPost(path, vet);
  }

  public updateVet(vet: Vet): Observable<Vet> {
    const path = ["api", "vet", vet.id].join("/");
    return this.restApiPut(path, vet);
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
