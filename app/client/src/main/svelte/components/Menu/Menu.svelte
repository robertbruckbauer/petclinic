<script>
  import { fly } from "svelte/transition";
  import { quadIn } from "svelte/easing";
  export let show = true;
</script>

{#if show}
<aside
  class="fixed top-0 left-0 w-48 h-screen mt-12 overflow-hidden pointer-events-none z-10"
  transition:fly={{
    duration: 200,
    x: -300,
    easing: quadIn,
    opacity: 1,
  }}
>
  <nav class="absolute flex h-full w-full bg-white pointer-events-auto overflow-y-auto">
    <div class="w-full">
      <slot />
    </div>
  </nav>
</aside>
{/if}
