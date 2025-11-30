import { map, Observable, from, switchMap } from "rxjs";
import type { Owner, OwnerItem } from "../types/owner.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class OwnerService extends BaseService {
  public loadAllOwner(query: string = ""): Observable<Owner[]> {
    const path = [backendUrl(), "api", "owner" + query].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: Owner[] }>),
      map((body: { content: Owner[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public loadAllOwnerItem(): Observable<OwnerItem[]> {
    const path = [backendUrl(), "api", "owner", "item"].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: OwnerItem[] }>),
      map((body: { content: OwnerItem[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public loadOneOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<Owner>),
      tapLog("GET", path)
    );
  }

  public createOwner(owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.restApiPost(path, owner).pipe(
      switchMap(this.mapResponseToObservable<Owner>),
      tapLog("POST", path, owner)
    );
  }

  public updateOwner(id: string, owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.restApiPut(path, owner).pipe(
      switchMap(this.mapResponseToObservable<Owner>),
      tapLog("PUT", path, owner)
    );
  }

  public removeOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.restApiDelete(path).pipe(
      switchMap(this.mapResponseToObservable<Owner>),
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
