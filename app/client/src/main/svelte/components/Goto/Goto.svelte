<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'disabled',
    'icon',
    'outlined',
    'page',
    'title'
  ], $$props);
  export let disabled = false;
  export let icon = "forward"
  export let outlined = false;
  export let page;
  export let target = "_self";
  export let title = undefined;
</script>

<a 
  class:disabled={disabled}
  href={page}
  {target}
  tabindex=-1
>
  <button
    {...props}
    {title}
    {disabled}
    class:disabled={disabled}
    class="w-full h-full text-sm text-white rounded uppercase py-2 px-4 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500 overflow-hidden"
    class:outlined={outlined}
    on:click
    on:mouseover
    on:focus
    on:blur
  >
    <div class="flex flex-row content-center justify-center items-center gap-1">
      <i
        {title}
        aria-hidden="true"
        class="material-icons icon text-xl select-none duration-200 ease-in"
        disabled
      >
        {icon}
      </i>
      <slot />
    </div>
  </button>
</a>

<style lang="postcss">
  a.disabled, button.disabled {
    user-select: none;
    pointer-events: none;
    cursor: default;
  }
  button.outlined {
    @apply border-2 border-solid border-primary-500 bg-transparent text-primary-500 font-bold;
  }
</style>
