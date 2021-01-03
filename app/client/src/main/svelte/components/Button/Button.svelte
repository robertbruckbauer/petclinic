<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'disabled',
    'outlined',
    'title'
  ], $$props);
  export let disabled = false;
  export let outlined = false;
  export let title = undefined;
</script>

<button
  {...props}
  {title}
  {disabled}
  class:disabled={disabled}
  class="text-sm text-white rounded uppercase py-2 px-4 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500 overflow-hidden"
  class:outlined={outlined}
  on:click
  on:mouseover
  on:focus
  on:blur
>
  <slot />
</button>

<style lang="postcss">
  button.disabled {
    user-select: none;
    pointer-events: none;
    cursor: default;
  }
  button.outlined {
    @apply border-2 border-solid border-primary-500 bg-transparent text-primary-500 font-bold;
  }
</style>
