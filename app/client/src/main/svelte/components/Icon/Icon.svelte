<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'disabled',
    'name',
    'outlined',
    'title'
  ], $$props);
  export let disabled = false;
  export let name = "help";
  export let outlined = false;
  export let title = undefined;
</script>

<button
  {...props}
  {title}
  {disabled}
  class:disabled={disabled}
  class="text-xl text-white w-12 h-12 rounded-full p-2 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500"
  class:outlined={outlined}
  on:click
  on:mouseover
  on:focus
  on:blur
>
  <div class="flex justify-center items-center">
    <i
      {title}
      aria-hidden="true"
      class="material-icons icon text-xl select-none"
      disabled
    >
      {name}
    </i>
  </div>
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
