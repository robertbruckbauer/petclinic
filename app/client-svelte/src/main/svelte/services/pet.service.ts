import { Observable } from "rxjs";
import type { Pet, PetItem } from "../types/pet.type";
import { BackendService } from "./backend.service";

export class PetService extends BackendService {
  public loadAllPet(search: Record<string, string> = {}): Observable<Pet[]> {
    const path = ["api", "pet"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadAllPetItem(): Observable<PetItem[]> {
    const path = ["api", "pet", "search", "findAllItem"].join("/");
    return this.restApiGetAll(path, {});
  }

  public loadOnePet(id: string): Observable<Pet> {
    const path = ["api", "pet", id].join("/");
    return this.restApiGet(path);
  }

  public createPet(pet: Pet): Observable<Pet> {
    const path = ["api", "pet"].join("/");
    return this.restApiPost(path, pet);
  }

  public mutatePet(id: string, value: Partial<Pet>): Observable<Pet> {
    const path = ["api", "pet", id].join("/");
    return this.restApiPatch(path, value);
  }

  public removePet(id: string): Observable<Pet> {
    const path = ["api", "pet", id].join("/");
    return this.restApiDelete(path);
  }
}

export function mapPetToPetItem(value: Pet): PetItem {
  return {
    value: value.id!,
    text: value.species + " " + value.name,
  };
}
