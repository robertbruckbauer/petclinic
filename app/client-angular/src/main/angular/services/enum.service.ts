import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map } from "rxjs";
import { backendUrl } from "../app.routes";
import { type EnumItem } from "../types/enum.type";
import { tapLog } from "../utils/log";

@Injectable()
export class EnumService {
  constructor(private httpClient: HttpClient) {}

  public loadAllEnum(art: string) {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return this.httpClient.get<{ content: EnumItem[] }>(path).pipe(
      tapLog("GET", path),
      map((body) => body.content)
    );
  }

  public createEnum(art: string, item: EnumItem) {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return this.httpClient
      .post<EnumItem>(path, item)
      .pipe(tapLog("POST", path, item));
  }

  public updateEnum(art: string, item: EnumItem) {
    const path = [backendUrl(), "api", "enum", art, item.code].join("/");
    return this.httpClient
      .put<EnumItem>(path, item)
      .pipe(tapLog("PUT", path, item));
  }

  public removeEnum(art: string, code: number) {
    const path = [backendUrl(), "api", "enum", art, code].join("/");
    return this.httpClient.delete<EnumItem>(path).pipe(tapLog("DELETE", path));
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
