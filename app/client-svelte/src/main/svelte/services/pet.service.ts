import { Observable } from "rxjs";
import type { Pet, PetItem } from "../types/pet.type";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class PetService extends BaseService {
  public loadAllPet(query: string = ""): Observable<Pet[]> {
    const path = [backendUrl(), "api", "pet" + query].join("/");
    return this.restApiGetAll(path);
  }

  public loadAllPetItem(owner: string): Observable<PetItem[]> {
    const path = [backendUrl(), "api", "pet", "item", owner].join("/");
    return this.restApiGetAll(path);
  }

  public loadOnePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.restApiGet(path);
  }

  public createPet(pet: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet"].join("/");
    return this.restApiPost(path, pet);
  }

  public updatePet(id: string, pet: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.restApiPut(path, pet);
  }

  public removePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.restApiDelete(path);
  }
}

export function mapPetToPetItem(value: Pet): PetItem {
  return {
    value: value.id!,
    text: value.species + " " + value.name,
  };
}
