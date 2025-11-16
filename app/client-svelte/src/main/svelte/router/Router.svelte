<script lang="ts" module>
  export {
    activeRoute,
    register,
    startRouter,
    stopRouter,
    navigate,
  } from "./router";
</script>

<script lang="ts">
  import { onMount, onDestroy } from "svelte";
  import { startRouter, stopRouter } from "./router";

  let { children } = $props();

  onMount(() => {
    startRouter();
  });

  onDestroy(() => {
    stopRouter();
  });
</script>

{@render children?.()}
