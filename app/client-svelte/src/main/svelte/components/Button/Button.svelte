<script lang="ts">
  let {
    checked = $bindable(false),
    clicked = $bindable(0),
    disabled = false,
    outlined = false,
    title = undefined,
    onclick = undefined,
    children,
    ...elementProps
  } = $props();

  let element: HTMLButtonElement;
  export function focus() {
    element?.focus();
  }

  function handleClick(_event: MouseEvent) {
    checked = !checked;
    clicked++;
    onclick?.(_event);
  }
</script>

<button
  type="button"
  bind:this={element}
  {...elementProps}
  {title}
  {disabled}
  class:disabled
  class="text-sm font-bold border-2 border-solid border-indigo-500 rounded-md uppercase py-2 px-4 disabled:opacity-50 hover:opacity-90 focus:underline overflow-hidden"
  class:outlined
  class:text-indigo-500={outlined}
  class:bg-transparent={outlined}
  class:text-white={!outlined}
  class:bg-indigo-500={!outlined}
  onclick={handleClick}
>
  {@render children?.()}
</button>
