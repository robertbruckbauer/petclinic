import { Observable, from, switchMap, throwError } from "rxjs";
import { type Version } from "../types/version.type";
import { type ErrorItem } from "../types/error.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";

export class VersionService {
  public version(): Observable<Response> {
    const path = [backendUrl(), "version"].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "text/html",
        },
      })
    ).pipe(
      switchMap((res) => {
        if (res.ok) {
          return from(Promise.resolve(res));
        }
        return throwError(() => new Error(`${path} failed with ${res.status}`));
      }),
      tapLog("GET", path)
    );
  }

  public apiExplorerUrl(): string {
    return backendUrl() + "/api/explorer";
  }

  public apiGraphiqlUrl(): string {
    return backendUrl() + "/api/graphiql";
  }
}
