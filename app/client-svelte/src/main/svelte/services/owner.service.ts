import { map, Observable, from, switchMap } from "rxjs";
import type { Owner, OwnerItem } from "../types/owner.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class OwnerService extends BaseService {
  public loadAllOwner(query: string = ""): Observable<Owner[]> {
    const path = [backendUrl(), "api", "owner" + query].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<{ content: Owner[] }>),
      tapLog("GET", path),
      map((body: { content: Owner[] }) => body.content)
    );
  }

  public loadAllOwnerItem(): Observable<OwnerItem[]> {
    const path = [backendUrl(), "api", "owner", "item"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<{ content: OwnerItem[] }>),
      tapLog("GET", path),
      map((body: { content: OwnerItem[] }) => body.content)
    );
  }

  public loadOneOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Owner>),
      tapLog("GET", path)
    );
  }

  public createOwner(owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(owner),
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Owner>),
      tapLog("POST", path, owner)
    );
  }

  public updateOwner(id: string, owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(owner),
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Owner>),
      tapLog("PUT", path, owner)
    );
  }

  public removeOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap(this.mapResponseToObservableJson<Owner>),
      tapLog("DELETE", path)
    );
  }
}

export function mapOwnerToOwnerItem(value: Owner): OwnerItem {
  return {
    value: value.id!,
    text: value.name + ", " + value.address,
  };
}
