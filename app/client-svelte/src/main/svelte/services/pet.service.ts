import { map, Observable, from, switchMap } from "rxjs";
import type { Pet, PetItem } from "../types/pet.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class PetService extends BaseService {
  public loadAllPet(query: string = ""): Observable<Pet[]> {
    const path = [backendUrl(), "api", "pet" + query].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<{ content: Pet[] }>),
      tapLog("GET", path),
      map((body: { content: Pet[] }) => body.content)
    );
  }

  public loadAllPetItem(owner: string): Observable<PetItem[]> {
    const path = [backendUrl(), "api", "pet", "item", owner].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<{ content: PetItem[] }>),
      tapLog("GET", path),
      map((body: { content: PetItem[] }) => body.content)
    );
  }

  public loadOnePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Pet>),
      tapLog("GET", path)
    );
  }

  public createPet(pet: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(pet),
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Pet>),
      tapLog("POST", path, pet)
    );
  }

  public updatePet(id: string, pet: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(pet),
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Pet>),
      tapLog("PUT", path, pet)
    );
  }

  public removePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Pet>),
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
