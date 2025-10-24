import { OperatorFunction, tap } from "rxjs";

export function tapLog<T>(
  method: string,
  path: string
): OperatorFunction<T, T> {
  return tap({
    next: (res) => {
      console.log([method, path, res]);
    },
    error: (err) => {
      console.log([method, path, err]);
    },
  });
}
