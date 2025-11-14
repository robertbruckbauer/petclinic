import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { backendUrl } from "../app.routes";
import { type Version } from "../types/version.type";
import { tapLog } from "../utils/log";

@Injectable()
export class VersionService {
  constructor(private httpClient: HttpClient) {}

  public loadVersion() {
    const path = backendUrl() + "/version";
    return this.httpClient.get<Version>(path).pipe(tapLog("GET", path));
  }
}
