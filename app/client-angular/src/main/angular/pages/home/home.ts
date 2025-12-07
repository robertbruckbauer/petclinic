import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from "@angular/core";
import { VersionService } from "../../services/version.service";

@Component({
  selector: "app-home",
  imports: [],
  templateUrl: "./home.html",
  styles: ``,
})
export class HomeComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private versionService = inject(VersionService);

  apiExplorer = signal<URL>(new URL(this.versionService.apiExplorerUrl()));
  apiExplorerHref = computed<string>(
    () => this.apiExplorer() + "/index.html#uri=/api"
  );

  apiGraphiql = signal<URL>(new URL(this.versionService.apiGraphiqlUrl()));
  apiGraphiqlHref = computed<string>(
    () => this.apiGraphiql() + "?path=/api/graphql"
  );

  version = signal("-");

  ngOnInit() {
    const subscription = this.versionService.loadVersion().subscribe({
      next: (res) => {
        this.version.set(res.version);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
}
