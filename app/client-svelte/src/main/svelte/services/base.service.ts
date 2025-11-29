import { from, switchMap, throwError, type Observable } from "rxjs";
import type { ErrorItem } from "../types/error.type";

export abstract class BaseService {
  protected mapResponseToObservableJson<T>(res: Response): Observable<T> {
    if (res.ok) {
      return from(res.json());
    }
    return from(res.json()).pipe(
      switchMap((err: ErrorItem) => throwError(() => err))
    );
  }
}
