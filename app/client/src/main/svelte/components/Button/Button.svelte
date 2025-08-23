<script>
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["checked", "clicked", "disabled", "outlined", "title"],
    $$props
  );
  export let checked = false;
  export let clicked = 0;
  export let disabled = false;
  export let outlined = false;
  export let title = undefined;
  let element;
  export function focus() {
    element.focus();
  }
  function onClick() {
    checked = !checked;
    clicked++;
  }
</script>

<button
  type="button"
  bind:this={element}
  {...props}
  {title}
  {disabled}
  class:disabled
  class="text-sm text-white rounded uppercase py-2 px-4 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500 overflow-hidden"
  class:outlined
  on:click={onClick}
  on:click
  on:mouseover
  on:focus
  on:blur
>
  <slot />
</button>
