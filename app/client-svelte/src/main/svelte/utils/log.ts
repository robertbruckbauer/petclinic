import { tap } from "rxjs";

export function tapLog<T>(method: string, path: string, body?: any) {
  return tap<T>(() => {
    if (import.meta.env.DEV) {
      const msg = body ? [method, path, body] : [method, path];
      console.log(msg);
    }
  });
}
