import { HttpErrorResponse } from "@angular/common/http";
import { throwError } from "rxjs";
import type { ErrorItem } from "../types/error.type";

export abstract class BaseService {
  protected handleError(path: string) {
    return (err: HttpErrorResponse) => {
      if (err.status === 0) {
        const error: ErrorItem = {
          instance: path,
          status: 0,
          title: "Network Error",
          detail: err.message,
        };
        return throwError(() => error);
      }
      return throwError(() => err.error as ErrorItem);
    };
  }
}
