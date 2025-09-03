<script lang="ts">
  let {
    disabled = false,
    label = undefined,
    resize = true,
    title = undefined,
    value = $bindable(),
    onchange = undefined,
    onfocus = undefined,
    onblur = undefined,
    ...elementProps
  } = $props();

  let element;
  export function focus() {
    element?.focus();
  }

  let valueInternal = $state(value);
  $effect(() => {
    valueInternal = value;
  });

  let focused = $state(false);
  function handleBlur(_event: FocusEvent) {
    focused = false;
    value = valueInternal;
    onblur?.(_event);
  }
  function handleFocus(_event: FocusEvent) {
    focused = true;
    onfocus?.(_event);
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
    {...elementProps}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!focused}
    class:resize-none={!resize}
    class:border-b={label}
    aria-label={label}
    bind:value={valueInternal}
    onfocus={handleFocus}
    onblur={handleBlur}
  >
    &nbsp;
  </textarea>
</div>
