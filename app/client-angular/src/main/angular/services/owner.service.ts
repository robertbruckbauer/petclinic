import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, map } from "rxjs";
import { type OwnerItem, type Owner } from "../types/owner.type";
import { BackendService } from "./backend.service";

@Injectable()
export class OwnerService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  public loadAllOwner(
    search: Record<string, string> = {}
  ): Observable<Owner[]> {
    const path = ["api", "owner"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadAllOwnerItem(): Observable<OwnerItem[]> {
    const path = ["api", "owner", "search", "findAllItem"].join("/");
    return this.restApiGetAll(path, {});
  }

  public loadOneOwner(id: string): Observable<Owner> {
    const path = ["api", "owner", id].join("/");
    return this.restApiGet(path);
  }

  public createOwner(value: Owner): Observable<Owner> {
    const path = ["api", "owner"].join("/");
    return this.restApiPost(path, value);
  }

  public mutateOwner(id: string, value: Partial<Owner>): Observable<Owner> {
    const path = ["api", "owner", id].join("/");
    return this.restApiPatch(path, value);
  }

  public removeOwner(id: string): Observable<Owner> {
    const path = ["api", "owner", id].join("/");
    return this.restApiDelete(path);
  }
}

export function mapOwnerToOwnerItem(value: Owner): OwnerItem {
  return {
    value: value.id!,
    text: value.name + ", " + value.address,
  };
}

export function compareOwnerItem(
  item1: OwnerItem | null,
  item2: OwnerItem | null
): boolean {
  return item1?.value === item2?.value;
}
