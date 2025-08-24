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
  class="text-xl text-white w-12 h-12 rounded-full p-2 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500"
  class:outlined
  onclick={handleClick}
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
