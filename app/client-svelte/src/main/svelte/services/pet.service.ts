import { map, Observable, from, switchMap, throwError } from "rxjs";
import { type Pet, type PetItem } from "../types/pet.type";
import { type ErrorItem } from "../types/error.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";

export class PetService {
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
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("GET", path),
      map((body: { content: Pet[] }) => body.content)
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
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("GET", path)
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
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
      tapLog("GET", path),
      map((body: { content: PetItem[] }) => body.content)
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
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
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
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
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
      switchMap((res) => {
        if (res.ok) {
          return from(res.json());
        }
        return from(res.json()).pipe(
          switchMap((error: ErrorItem) => throwError(() => error))
        );
      }),
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
