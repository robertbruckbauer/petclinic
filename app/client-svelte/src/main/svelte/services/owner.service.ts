import { Observable } from "rxjs";
import type { Owner, OwnerItem } from "../types/owner.type";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class OwnerService extends BaseService {
  public loadAllOwner(
    search: Record<string, string> = {}
  ): Observable<Owner[]> {
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadAllOwnerItem(
    search: Record<string, string> = {}
  ): Observable<OwnerItem[]> {
    const path = [backendUrl(), "api", "owner", "item"].join("/");
    return this.restApiGetAll(path, search);
  }

  public loadOneOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.restApiGet(path);
  }

  public createOwner(owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner"].join("/");
    return this.restApiPost(path, owner);
  }

  public updateOwner(owner: Owner): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", owner.id].join("/");
    return this.restApiPut(path, owner);
  }

  public removeOwner(id: string): Observable<Owner> {
    const path = [backendUrl(), "api", "owner", id].join("/");
    return this.restApiDelete(path);
  }
}

export function mapOwnerToOwnerItem(value: Owner): OwnerItem {
  return {
    value: value.id!,
    text: value.name + ", " + value.address,
  };
}
