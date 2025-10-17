import { DestroyRef, Injectable, inject, signal } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class RestService {
  private destroyRef = inject(DestroyRef);
  private httpClient = inject(HttpClient);

  public loadVersion() {
    const html = signal("-");
    const path = backendUrl() + "/version";
    const subscription = this.httpClient
      .get<{ version: string }>(path)
      .subscribe({
        next: (res) => {
          console.log(["version", res]);
          html.set(res.version);
        },
        error: (err) => {
          console.log(["version", err]);
        },
      });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
    return html;
  }
}

export const backendUrl = () => {
  return (
    window.location.protocol +
    "//" +
    window.location.host.replace("5052", "8080")
  );
};
