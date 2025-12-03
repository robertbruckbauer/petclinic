import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { catchError, tap } from "rxjs";
import { backendUrl } from "../app.routes";
import { type Version } from "../types/version.type";
import { BaseService } from "./base.service";

@Injectable()
export class VersionService extends BaseService {
  constructor(private httpClient: HttpClient) {
    super();
  }

  public loadVersion() {
    const path = backendUrl() + "/version";
    return this.httpClient.get<Version>(path).pipe(
      catchError(this.handleError(path)),
      tap((body) => {
        console.log([["GET", path].join(" "), body]);
      })
    );
  }
}
