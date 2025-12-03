import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, map, Observable, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type PetItem, type Pet } from "../types/pet.type";
import { BaseService } from "./base.service";

@Injectable()
export class PetService extends BaseService {
  constructor(private httpClient: HttpClient) {
    super();
  }

  public loadAllPet(search: Record<string, string> = {}): Observable<Pet[]> {
    const path = [backendUrl(), "api", "pet"].join("/");
    const params = new HttpParams({ fromObject: search });
    return this.httpClient.get<{ content: Pet[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public loadAllPetItem(): Observable<PetItem[]> {
    const params = new HttpParams().set("sort", "name,asc");
    const path = [backendUrl(), "api", "pet"].join("/");
    return this.httpClient.get<{ content: Pet[] }>(path, { params }).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content.map(mapPetToPetItem)),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public createPet(value: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet"].join("/");
    return this.httpClient.post<Pet>(path, value).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["POST", path].join(" "), value, body]);
      })
    );
  }

  public updatePet(value: Pet): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", value.id].join("/");
    const headers = { "Content-Type": "application/merge-patch+json" };
    return this.httpClient.patch<Pet>(path, value, { headers }).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["PATCH", path].join(" "), value, body]);
      })
    );
  }

  public removePet(id: string): Observable<Pet> {
    const path = [backendUrl(), "api", "pet", id].join("/");
    return this.httpClient.delete<Pet>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["DELETE", path].join(" "), body]);
      })
    );
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
