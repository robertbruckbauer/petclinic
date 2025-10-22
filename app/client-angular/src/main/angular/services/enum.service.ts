import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type EnumItem } from "../types/enum.type";

@Injectable()
export class EnumService {
  private httpClient = inject(HttpClient);

  public loadAllEnum(art: string) {
    const path = backendUrl() + "/enum/" + art;
    return this.httpClient.get<{ content: EnumItem[] }>(path).pipe(
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
