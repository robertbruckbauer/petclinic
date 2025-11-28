import { map, Observable, from, switchMap, throwError } from "rxjs";
import { type EnumItem } from "../types/enum.type";
import { type ErrorItem } from "../types/error.type";
import { tapLog } from "../utils/log";
import { backendUrl } from "../router/router";

export class EnumService {
  public loadAllEnum(art: string): Observable<EnumItem[]> {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap((res) => {
        const json$ = from(res.json());
        if (res.ok) {
          return json$;
        } else {
          return json$.pipe(
            switchMap((err: ErrorItem) => throwError(() => err))
          );
        }
      }),
      tapLog("GET", path),
      map((body: { content: EnumItem[] }) => body.content)
    );
  }

  public createEnum(art: string, item: EnumItem): Observable<EnumItem> {
    const path = [backendUrl(), "api", "enum", art].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(item),
      })
    ).pipe(
      switchMap((res) => {
        const json$ = from(res.json());
        if (res.ok) {
          return json$;
        } else {
          return json$.pipe(
            switchMap((err: ErrorItem) => throwError(() => err))
          );
        }
      }),
      tapLog("POST", path, item)
    );
  }

  public updateEnum(art: string, item: EnumItem): Observable<EnumItem> {
    const path = [backendUrl(), "api", "enum", art, item.code].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-type": "application/json",
        },
        body: JSON.stringify(item),
      })
    ).pipe(
      switchMap((res) => {
        const json$ = from(res.json());
        if (res.ok) {
          return json$;
        } else {
          return json$.pipe(
            switchMap((err: ErrorItem) => throwError(() => err))
          );
        }
      }),
      tapLog("PUT", path, item)
    );
  }

  public removeEnum(art: string, code: string | number): Observable<EnumItem> {
    const path = [backendUrl(), "api", "enum", art, code].join("/");
    return from(
      fetch(path, {
        mode: "cors",
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })
    ).pipe(
      switchMap((res) => {
        const json$ = from(res.json());
        if (res.ok) {
          return json$;
        } else {
          return json$.pipe(
            switchMap((err: ErrorItem) => throwError(() => err))
          );
        }
      }),
      tapLog("DELETE", path)
    );
  }
}

export function filterByCriteria(criteria: string | null | undefined) {
  return (item: EnumItem) => {
    if (criteria) {
      if (item.name.toLowerCase().startsWith(criteria.toLowerCase()))
        return true;
      if (item.text.toLowerCase().startsWith(criteria.toLowerCase()))
        return true;
      return false;
    }
    return true;
  };
}

export function compareEnumItem(
  item1: EnumItem | null,
  item2: EnumItem | null
): boolean {
  return item1?.code === item2?.code;
}
