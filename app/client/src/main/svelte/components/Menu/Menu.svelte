<script>
  import { fly } from "svelte/transition";
  import { quadIn } from "svelte/easing";
  let { show = true, children } = $props();
</script>

{#if show}
  <aside
    class="w-72 h-full pointer-events-none"
    transition:fly={{
      duration: 200,
      x: -300,
      easing: quadIn,
      opacity: 1,
    }}
  >
    <nav class="absolute flex w-full h-full pointer-events-auto z-10 bg-white">
      <div class="w-full">
        {@render children?.()}
      </div>
    </nav>
  </aside>
{/if}
