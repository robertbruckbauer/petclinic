import { Observable, map, switchMap } from "rxjs";
import type { EnumItem } from "../types/enum.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class EnumService extends BaseService {
  public loadAllEnum(art: string): Observable<EnumItem[]> {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return this.restApiGet(path).pipe(
      switchMap(this.mapResponseToObservable<{ content: EnumItem[] }>),
      map((body: { content: EnumItem[] }) => body.content),
      tapLog("GET", path)
    );
  }

  public createEnum(art: string, item: EnumItem): Observable<EnumItem> {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return this.restApiPost(path, item).pipe(
      switchMap(this.mapResponseToObservable<EnumItem>),
      tapLog("POST", path, item)
    );
  }

  public updateEnum(art: string, item: EnumItem): Observable<EnumItem> {
    const path = [backendUrl(), "api", "enum", art, item.code].join("/");
    return this.restApiPut(path, item).pipe(
      switchMap(this.mapResponseToObservable<EnumItem>),
      tapLog("PUT", path, item)
    );
  }

  public removeEnum(art: string, code: string | number): Observable<EnumItem> {
    const path = [backendUrl(), "api", "enum", art, code].join("/");
    return this.restApiDelete(path).pipe(
      switchMap(this.mapResponseToObservable<EnumItem>),
      tapLog("DELETE", path)
    );
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
