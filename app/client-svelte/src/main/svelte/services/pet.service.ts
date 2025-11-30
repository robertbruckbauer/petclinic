import { Observable, map, switchMap } from "rxjs";
import type { Pet, PetItem } from "../types/pet.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class PetService extends BaseService {
  public loadAllPet(query: string = ""): Observable<Pet[]> {
    const path = [backendUrl(), "api", "pet" + query].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: Pet[] }>),
      map((body: { content: Pet[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public loadAllPetItem(owner: string): Observable<PetItem[]> {
    const path = [backendUrl(), "api", "pet", "item", owner].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: PetItem[] }>),
      map((body: { content: PetItem[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public loadOnePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<Pet>),
      tapLog("GET", path)
    );
  }

  public createPet(pet: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet"].join("/");
    return this.restApiPost(path, pet).pipe(
      switchMap(this.mapResponseToObservable<Pet>),
      tapLog("POST", path, pet)
    );
  }

  public updatePet(id: string, pet: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.restApiPut(path, pet).pipe(
      switchMap(this.mapResponseToObservable<Pet>),
      tapLog("PUT", path, pet)
    );
  }

  public removePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.restApiDelete(path).pipe(
      switchMap(this.mapResponseToObservable<Pet>),
      tapLog("DELETE", path)
    );
  }
}

export function mapPetToPetItem(value: Pet): PetItem {
  return {
    value: value.id!,
    text: value.species + " " + value.name,
  };
}
