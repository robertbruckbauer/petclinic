<script>
  import * as restApi from "../services/rest.js";
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";

  let apiExplorer = restApi.apiExplorerUrl();

  let apiGraphiql = restApi.apiGraphiqlUrl();

  let versionHtml = "<span>loading ..</span>";

  onMount(async () => {
    restApi
      .version()
      .then((res) => res.text())
      .then((html) => {
        versionHtml = html;
      })
      .catch((err) => {
        console.log(err);
        toast.push(err.toString());
      });
  });
</script>

<h1>Info</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2">
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">APP-Logo</legend>
    <div class="text-2xl">
      <img src="/pets.png" alt="Pets" />
    </div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">APP-Version</legend>
    <div class="text-2xl">
      {#if versionHtml}
        {@html versionHtml}
      {:else}
        -
      {/if}
    </div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">API-Explorer</legend>
    <div class="text-2xl">
      <a
        class="underline text-blue-600"
        href="{apiExplorer}/index.html#uri=/api"
        target="_blank"
        >{apiExplorer}
      </a>
    </div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">API-Graphiql</legend>
    <div class="text-2xl">
      <a
        class="underline text-blue-600"
        href="{apiGraphiql}?path=/api/graphql"
        target="_blank"
        >{apiGraphiql}
      </a>
    </div>
  </fieldset>
</div>
