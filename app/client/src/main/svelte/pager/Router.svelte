<script lang="ts" module>
  import { writable } from "svelte/store";
  interface RouteData {
    path?: string;
    component?: any;
    params?: Record<string, any>;
  }
  export const activeRoute = writable<RouteData>({});
  const routes: Record<string, any> = {};
  export function register(route: any) {
    routes[route.path] = route;
  }
</script>

<script lang="ts">
  import { onMount, onDestroy } from "svelte";
  import page from "page";

  let { children } = $props();

  onMount(() => {
    for (let [path, route] of Object.entries(routes)) {
      page(
        path,
        (ctx: any) => ($activeRoute = { ...route, params: ctx.params })
      );
    }
    page.start();
  });

  onDestroy(page.stop());
</script>

{@render children?.()}
