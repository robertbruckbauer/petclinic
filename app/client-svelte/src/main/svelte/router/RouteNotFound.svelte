<script lang="ts">
  import { register, activeRoute } from "./Router.svelte";

  let { path = "*", children } = $props();

  $effect(() => {
    register({ path });
  });
</script>

{#if $activeRoute.path === path}
  {@render children?.()}
{/if}
