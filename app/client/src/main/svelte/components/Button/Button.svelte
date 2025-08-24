<script lang="ts">
  let {
    checked = $bindable(false),
    clicked = $bindable(0),
    disabled = false,
    outlined = false,
    title = undefined,
    onclick,
    children,
    ...elementProps
  } = $props();

  let element;
  export function focus() {
    element?.focus();
  }

  function handleClick(event: MouseEvent) {
    checked = !checked;
    clicked++;
    onclick?.(event);
  }
</script>

<button
  type="button"
  bind:this={element}
  {...elementProps}
  {title}
  {disabled}
  class:disabled
  class="text-sm text-white rounded uppercase py-2 px-4 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500 overflow-hidden"
  class:outlined
  onclick={handleClick}
>
  {@render children?.()}
</button>
