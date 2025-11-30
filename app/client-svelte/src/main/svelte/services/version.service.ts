import { Observable } from "rxjs";
import type { Version } from "../types/version.type";
import { backendUrl } from "../router/router";
import { BaseService } from "./base.service";

export class VersionService extends BaseService {
  public version(): Observable<Version> {
    const path = [backendUrl(), "version"].join("/");
    return this.restApiGet(path);
  }

  public apiExplorerUrl(): string {
    return backendUrl() + "/api/explorer";
  }

  public apiGraphiqlUrl(): string {
    return backendUrl() + "/api/graphiql";
  }
}
