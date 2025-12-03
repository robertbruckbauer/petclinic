import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { catchError, map, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type EnumItem } from "../types/enum.type";
import { BaseService } from "./base.service";

@Injectable()
export class EnumService extends BaseService {
  constructor(private httpClient: HttpClient) {
    super();
  }

  public loadAllEnum(art: string) {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return this.httpClient.get<{ content: EnumItem[] }>(path).pipe(
      catchError(this.handleError(path)),
      map((body) => body.content),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }

  public createEnum(art: string, item: EnumItem) {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return this.httpClient.post<EnumItem>(path, item).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["POST", path].join(" "), item, body]);
      })
    );
  }

  public updateEnum(art: string, item: EnumItem) {
    const path = [backendUrl(), "api", "enum", art, item.code].join("/");
    return this.httpClient.put<EnumItem>(path, item).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["PUT", path].join(" "), item, body]);
      })
    );
  }

  public removeEnum(art: string, code: number) {
    const path = [backendUrl(), "api", "enum", art, code].join("/");
    return this.httpClient.delete<EnumItem>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["DELETE", path].join(" "), body]);
      })
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
