<script lang="ts">
  import { register, activeRoute } from "./Router.svelte";

  let { path = "*", component = null, children, ...restProps } = $props();

  register({ path, component });

  const params = $derived(
    $activeRoute.path === path ? $activeRoute.params : {}
  );
</script>

{#if $activeRoute.path === path}
  {#if $activeRoute.component}
    {@const Component = $activeRoute.component}
    <Component {...restProps} {...params} />
  {:else}
    {@render children?.(params)}
  {/if}
{/if}
