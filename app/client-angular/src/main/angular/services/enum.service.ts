import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type EnumItem } from "../types/enum.type";

@Injectable()
export class EnumService {
  private httpClient = inject(HttpClient);

  public loadAllEnum(art: string, criteria: string | null | undefined) {
    const path = [backendUrl(), "enum", art].join("/");
    return this.httpClient.get<{ content: EnumItem[] }>(path).pipe(
      tap({
        next: (res) => {
          console.log([path, res]);
        },
        error: (err) => {
          console.log([path, err]);
        },
      }),
      map((res) =>
        res.content.filter((e) => {
          if (criteria) {
            if (e.name.toLowerCase().startsWith(criteria.toLowerCase()))
              return true;
            if (e.text.toLowerCase().startsWith(criteria.toLowerCase()))
              return true;
            return false;
          }
          return true;
        })
      )
    );
  }

  public removeEnum(art: string, code: number) {
    const path = [backendUrl(), "enum", art, code].join("/");
    return this.httpClient.delete<EnumItem>(path).pipe(
      tap({
        next: (res) => {
          console.log([path, res]);
        },
        error: (err) => {
          console.log([path, err]);
        },
      })
    );
  }
}
