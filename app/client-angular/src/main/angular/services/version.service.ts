import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type Version } from "../types/version.type";

@Injectable()
export class VersionService {
  private httpClient = inject(HttpClient);

  public loadVersion() {
    const path = backendUrl() + "/version";
    return this.httpClient.get<Version>(path).pipe(
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
