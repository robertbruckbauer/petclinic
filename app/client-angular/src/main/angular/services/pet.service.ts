import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { backendUrl } from "../app.routes";
import { type PetItem, type Pet } from "../types/pet.type";
import { tapLog } from "../utils/log";
import { map } from "rxjs";

@Injectable()
export class PetService {
  private httpClient = inject(HttpClient);

  public loadAllPet(params: HttpParams | undefined = undefined) {
    const path = [backendUrl(), "api", "pet"].join("/");
    return this.httpClient.get<{ content: Pet[] }>(path, { params }).pipe(
      tapLog("GET", path),
      map((body) => body.content)
    );
  }

  public createPet(value: Pet) {
    const path = [backendUrl(), "api", "pet"].join("/");
    return this.httpClient.post<Pet>(path, value).pipe(tapLog("POST", path));
  }

  public updatePet(value: Pet) {
    const path = [backendUrl(), "api", "pet", value.id].join("/");
    return this.httpClient.put<Pet>(path, value).pipe(tapLog("PUT", path));
  }

  public removePet(id: string) {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.httpClient.delete<Pet>(path).pipe(tapLog("DELETE", path));
  }
}

export function mapPetToItem(value: Pet): PetItem {
  return {
    value: value.id!,
    text: value.species + " " + value.name,
  };
}
