import { DestroyRef, Injectable, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable()
export class VersionService {
  private destroyRef = inject(DestroyRef);
  private httpClient = inject(HttpClient);

  public loadVersion() {
    const version = signal("-");
    const path = backendUrl() + "/version";
    const subscription = this.httpClient
      .get<{ version: string }>(path)
      .subscribe({
        next: (res) => {
          console.log(["version", res]);
          version.set(res.version);
        },
        error: (err) => {
          console.log(["version", err]);
        },
      });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
    return version;
  }
}

export const backendUrl = () => {
  return (
    window.location.protocol +
    "//" +
    window.location.host.replace("5052", "8080")
  );
};
