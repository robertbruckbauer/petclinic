<script>
    import { register, activeRoute } from './Router.svelte';
    export let path = '*';
    export let component = null;
    register({ path, component });
    let params = {};
    $: if ($activeRoute.path === path) {
        params = $activeRoute.params;
    }
</script>

{#if $activeRoute.path === path}
    {#if $activeRoute.component}
        <svelte:component
            this="{$activeRoute.component}"
            {...$$restProps}
            {...params}
        />
    {:else}
        <slot {params} />
    {/if}
{/if}