import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map } from "rxjs";
import { type PetItem, type Pet } from "../types/pet.type";
import { BackendService } from "./backend.service";

@Injectable()
export class PetService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

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

  public createPet(value: Pet): Observable<Pet> {
    const path = ["api", "pet"].join("/");
    return this.restApiPost(path, value);
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

export function comparePetItem(
  item1: PetItem | null,
  item2: PetItem | null
): boolean {
  return item1?.value === item2?.value;
}
