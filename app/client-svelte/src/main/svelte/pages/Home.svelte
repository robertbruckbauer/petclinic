<script lang="ts">
  import { onMount } from "svelte";
  import { VersionService } from "../services/version.service";
  import { toast } from "../components/Toast";

  const versionService = new VersionService();

  let apiExplorer = versionService.apiExplorerUrl();

  let apiGraphiql = versionService.apiGraphiqlUrl();

  let version = $state("-");

  onMount(async () => {
    versionService.loadVersion().subscribe({
      next: (json) => {
        version = json.version;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  });
</script>

<h1>Info</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2">
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">APP-Banner</legend>
    <div class="text-2xl">
      <img src="/pets.png" alt="Pets" />
    </div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">APP-Version</legend>
    <div class="text-2xl">{version}</div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">API-Explorer</legend>
    <div class="text-2xl">
      <a
        class="underline text-blue-600 break-all"
        href="{apiExplorer}/index.html#uri=/api"
        target="_blank"
        >{apiExplorer.pathname}
      </a>
    </div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">API-Graphiql</legend>
    <div class="text-2xl">
      <a
        class="underline text-blue-600 break-all"
        href="{apiGraphiql}?path=/api/graphql"
        target="_blank"
        >{apiGraphiql.pathname}
      </a>
    </div>
  </fieldset>
</div>
