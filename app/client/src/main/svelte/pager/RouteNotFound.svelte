<script lang="ts">
  import { register, activeRoute } from "./Router.svelte";

  let { path = "*", component = null, children } = $props();

  register({ path, component });
</script>

{#if $activeRoute.path === path}
  {#if component}
    {@const Component = component}
    <Component />
  {/if}
  {@render children?.()}
{/if}
