import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from "@angular/core";
import { VersionService } from "../../services/version.service";
import { backendUrl } from "../../app.routes";

@Component({
  selector: "app-home",
  imports: [],
  templateUrl: "./home.html",
  styleUrl: "./home.css",
})
export class HomeComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private restApi = inject(VersionService);

  apiExplorer = signal<string>(backendUrl() + "/api/explorer");
  apiExplorerHref = computed<string>(
    () => this.apiExplorer() + "/index.html#uri=/api"
  );

  apiGraphiql = signal<string>(backendUrl() + "/api/graphiql");
  apiGraphiqlHref = computed<string>(
    () => this.apiGraphiql() + "?path=/api/graphql"
  );

  version = signal("-");

  ngOnInit() {
    const subscription = this.restApi.loadVersion().subscribe({
      next: (res) => {
        this.version.set(res.version);
      },
    });
    this.destroyRef.onDestroy(() => {
      subscription.unsubscribe();
    });
  }
}
