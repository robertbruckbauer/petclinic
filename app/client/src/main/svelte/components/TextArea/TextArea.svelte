<script>
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["disabled", "label", "resize", "title", "value"],
    $$props
  );
  export let disabled = false;
  export let label = undefined;
  export let resize = true;
  export let title = undefined;
  export let value;
  let focused;
  let element;
  export function focus() {
    element.focus();
  }
  $: valueInternal = value;
  function onBlur() {
    value = valueInternal;
    dispatch("change", value);
  }
</script>

<div class="mt-1 relative">
  {#if label}
    <span
      class="px-4 pt-2 text-xs absolute left-0 top-0"
      class:text-label-600={!focused}
      class:text-primary-500={focused}
    >
      {label}
    </span>
  {/if}
  <textarea
    bind:this={element}
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!focused}
    class:resize-none={!resize}
    class:border-b={label}
    aria-label={label}
    bind:value={valueInternal}
    on:input
    on:keydown
    on:keypress
    on:keyup
    on:click
    on:focus={() => (focused = true)}
    on:focus
    on:blur={() => (focused = false)}
    on:blur={onBlur}
    on:blur
  >
    &nbsp;
  </textarea>
</div>
