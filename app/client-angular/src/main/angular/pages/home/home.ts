import { Component, computed, inject, signal } from "@angular/core";
import { backendUrl, VersionService } from "../../services/version";

@Component({
  selector: "app-home",
  imports: [],
  templateUrl: "./home.html",
  styleUrl: "./home.css",
})
export class HomeComponent {
  private restApi = inject(VersionService);

  protected apiExplorer = signal<string>(backendUrl() + "/api/explorer");
  protected apiExplorerHref = computed<string>(
    () => this.apiExplorer() + "/index.html#uri=/api"
  );

  protected apiGraphiql = signal<string>(backendUrl() + "/api/graphiql");
  protected apiGraphiqlHref = computed<string>(
    () => this.apiGraphiql() + "?path=/api/graphql"
  );

  protected version = this.restApi.loadVersion();
}
