<script>
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["checked", "clicked", "disabled", "name", "outlined", "title"],
    $$props
  );
  export let checked = false;
  export let clicked = 0;
  export let disabled = false;
  export let name;
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
  class="text-xl text-white w-12 h-12 rounded-full p-2 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500"
  class:outlined
  on:click={onClick}
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
    >
      {name}
    </i>
  </div>
</button>
