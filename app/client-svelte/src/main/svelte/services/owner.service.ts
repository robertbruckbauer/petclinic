import { Observable } from "rxjs";
import type { Owner, OwnerItem } from "../types/owner.type";
import { BackendService } from "./backend.service";

export class OwnerService extends BackendService {
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

  public createOwner(owner: Owner): Observable<Owner> {
    const path = ["api", "owner"].join("/");
    return this.restApiPost(path, owner);
  }

  public updateOwner(owner: Owner): Observable<Owner> {
    const path = ["api", "owner", owner.id].join("/");
    return this.restApiPut(path, owner);
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
