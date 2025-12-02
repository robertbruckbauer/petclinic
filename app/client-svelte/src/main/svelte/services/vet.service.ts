import { Observable } from "rxjs";
import type { Vet, VetItem } from "../types/vet.type";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VetService extends BaseService {
  public loadAllVet(query: string = ""): Observable<Vet[]> {
    const path = [backendUrl(), "api", "vet" + query].join("/");
    return this.restApiGetAll(path);
  }

  public loadAllVetItem(): Observable<VetItem[]> {
    const path = [backendUrl(), "api", "vet", "item"].join("/");
    return this.restApiGetAll(path);
  }

  public loadOneVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.restApiGet(path);
  }

  public createVet(vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet"].join("/");
    return this.restApiPost(path, vet);
  }

  public updateVet(vet: Vet): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", vet.id].join("/");
    return this.restApiPut(path, vet);
  }

  public removeVet(id: string): Observable<Vet> {
    const path = [backendUrl(), "api", "vet", id].join("/");
    return this.restApiDelete(path);
  }
}

export function mapVetToVetItem(value: Vet): VetItem {
  return {
    value: value.id!,
    text: value.name,
  };
}
