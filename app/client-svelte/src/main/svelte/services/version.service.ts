import { Observable } from "rxjs";
import type { Version } from "../types/version.type";
import { BackendService } from "./backend.service";

export class VersionService extends BackendService {
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
