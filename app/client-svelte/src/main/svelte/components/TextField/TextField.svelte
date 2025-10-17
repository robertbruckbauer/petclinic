<script lang="ts">
  let {
    disabled = false,
    label = undefined,
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

  let elementIsFocused = $state(false);
  function handleFocus(_event: FocusEvent) {
    elementIsFocused = true;
    onfocus?.(_event);
  }
  function handleBlur(_event: FocusEvent) {
    elementIsFocused = false;
    onblur?.(_event);
  }

  let valueInternal = $state(value);
  $effect(() => {
    valueInternal = value;
  });
  function handleChange(_event: Event) {
    const target = _event.target as HTMLInputElement;
    value = target.value;
    onchange?.(_event);
  }
</script>

<div class="mt-1 relative">
  {#if label}
    <span
      class="px-4 pt-2 text-xs absolute left-0 top-0"
      class:text-label-600={!elementIsFocused}
      class:text-indigo-500={elementIsFocused}
    >
      {label}
    </span>
  {/if}
  <input
    type="text"
    bind:this={element}
    {...elementProps}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!elementIsFocused}
    class:border-b={label}
    aria-label={label}
    bind:value={valueInternal}
    onchange={handleChange}
    onfocus={handleFocus}
    onblur={handleBlur}
  />
</div>
