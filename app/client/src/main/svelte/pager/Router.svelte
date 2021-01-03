<script context="module">
    import { writable } from 'svelte/store';
    export const activeRoute = writable({});
    const routes = {};  
    export function register(route) {
        routes[route.path] = route;
    }
</script>
<script>
    import { onMount, onDestroy } from "svelte";
    import page from 'page';
    onMount(() => {
        for (let [path, route] of Object.entries(routes)) {
            page(path, (ctx) => ($activeRoute = { ...route, params: ctx.params }));
        }
        page.start();
    });
    onDestroy(page.stop());
</script>
<slot />