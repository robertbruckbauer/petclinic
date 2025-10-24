import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map } from "rxjs";
import { backendUrl } from "../app.routes";
import { type EnumItem } from "../types/enum.type";
import { tapLog } from "../utils/log";

@Injectable()
export class EnumService {
  private httpClient = inject(HttpClient);

  public loadAllEnum(art: string, criteria: string | null | undefined) {
    const path = [backendUrl(), "enum", art].join("/");
    return this.httpClient.get<{ content: EnumItem[] }>(path).pipe(
      tapLog("GET", path),
      map((body) => body.content.filter(filterByCriteria(criteria)))
    );
  }

  public createEnum(art: string, item: EnumItem) {
    const path = [backendUrl(), "enum", art].join("/");
    return this.httpClient
      .post<EnumItem>(path, item)
      .pipe(tapLog("POST", path));
  }

  public updateEnum(art: string, item: EnumItem) {
    const path = [backendUrl(), "enum", art, item.code].join("/");
    return this.httpClient.put<EnumItem>(path, item).pipe(tapLog("PUT", path));
  }

  public removeEnum(art: string, code: number) {
    const path = [backendUrl(), "enum", art, code].join("/");
    return this.httpClient.delete<EnumItem>(path).pipe(tapLog("DELETE", path));
  }
}

function filterByCriteria(criteria: string | null | undefined) {
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
