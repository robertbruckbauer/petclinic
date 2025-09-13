<script lang="ts">
  import { register, activeRoute } from "./Router.svelte";

  let { path = "*", children } = $props();

  register({ path });
</script>

{#if $activeRoute.path === path}
  {@render children?.()}
{/if}
