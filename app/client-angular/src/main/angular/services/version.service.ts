import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { type Version } from "../types/version.type";
import { BackendService } from "./backend.service";

@Injectable()
export class VersionService extends BackendService {
  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  public loadVersion(): Observable<Version> {
    const path = "/version";
    return this.restApiGet(path);
  }

  public apiExplorerUrl(): URL {
    return new URL("api/explorer", this.backendUrl());
  }

  public apiGraphiqlUrl(): URL {
    return new URL("api/graphiql", this.backendUrl());
  }
}
