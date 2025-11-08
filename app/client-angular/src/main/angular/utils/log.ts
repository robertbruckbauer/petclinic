import { OperatorFunction, tap } from "rxjs";

export function tapLog<T>(
  method: string,
  path: string,
  reqBody: any | undefined = undefined
): OperatorFunction<T, T> {
  return tap({
    next: (resBody) => {
      console.log([method, path, reqBody, resBody]);
    },
    error: (err) => {
      console.log([method, path, err]);
    },
  });
}
