import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { type EnumItem } from "../types/enum.type";
import { BackendService } from "./backend.service";

@Injectable()
export class EnumService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  public loadAllEnum(art: string): Observable<EnumItem[]> {
    const path = ["api", "enum", art].join("/");
    return this.restApiGetAll(path, {});
  }

  public createEnum(art: string, item: EnumItem): Observable<EnumItem> {
    const path = ["api", "enum", art].join("/");
    return this.restApiPost(path, item);
  }

  public updateEnum(art: string, item: EnumItem): Observable<EnumItem> {
    const path = ["api", "enum", art, item.code].join("/");
    return this.restApiPut(path, item);
  }

  public removeEnum(art: string, code: number): Observable<EnumItem> {
    const path = ["api", "enum", art, code].join("/");
    return this.restApiDelete(path);
  }
}

export function filterByCriteria(criteria: string | null | undefined) {
  return (item: EnumItem) => {
    if (criteria) {
      if (item.name.toLowerCase().startsWith(criteria.toLowerCase()))
        return true;
      if (item.text.toLowerCase().startsWith(criteria.toLowerCase()))
        return true;
      return false;
    }
    return true;
  };
}

export function compareEnumItem(
  item1: EnumItem | null,
  item2: EnumItem | null
): boolean {
  return item1?.code === item2?.code;
}
