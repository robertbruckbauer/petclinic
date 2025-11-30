import { tap } from "rxjs";

export function tapLog<T>(method: string, path: string, req?: any) {
  return tap<T>((res) => {
    console.log([[method, path].join(" "), req || {}, res || {}]);
  });
}
