<script lang="ts">
  import { tick } from "svelte";

  let {
    allItem,
    allValue = $bindable(),
    disabled = false,
    label = undefined,
    title = undefined,
    onchange = undefined,
    onfocus = undefined,
    onblur = undefined,
    ...elementProps
  } = $props();

  let _element;
  export function focus() {
    _element?.focus();
  }

  const allValueProcessed = $derived(allValue.map(valueMapper));

  function valueMapper(value: any) {
    if (typeof value !== "object") {
      return value;
    } else {
      return value ? JSON.stringify(value) : null;
    }
  }

  const allItemProcessed = $derived(allItem.map(itemMapper));

  function itemMapper(item: any) {
    if (typeof item !== "object") {
      return {
        value: item,
        text: item,
      };
    } else {
      return {
        value: valueMapper(item.value),
        text: item.text,
      };
    }
  }

  async function handleChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    let item = allItem[target.selectedIndex];
    let itemProcessed = allItemProcessed[target.selectedIndex];
    let itemIndex = allValueProcessed.findIndex(
      (e: any) => e === itemProcessed.value
    );
    if (itemIndex === -1) {
      if (typeof item !== "object") {
        allValue.push(item);
      } else {
        allValue.push(item.value);
      }
    } else {
      allValue.splice(itemIndex, 1);
    }
    // Trigger reactivity
    allValue = allValue;
    // Clear value to get every change event
    _element.value = null;
    // Delegate to parent
    await tick();
    onchange?.(event);
  }

  const allKeyIgnore = ["ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown"];
  const allKeyDelete = ["Backspace", "Delete"];
  function handleKeydown(event: KeyboardEvent) {
    if (allKeyIgnore.includes(event.key)) {
      event.preventDefault();
      return;
    }
    if (allKeyDelete.includes(event.key)) {
      allValue.pop();
      // Trigger reactivity
      allValue = allValue;
      return;
    }
  }

  let focused = $state(false);
  function handleFocus(event: FocusEvent) {
    focused = true;
    onfocus?.(event);
  }
  function handleBlur(event: FocusEvent) {
    focused = false;
    onblur?.(event);
  }
</script>

<div class="mt-1 relative">
  <div
    class="flex flex-col px-4 pt-2 absolute left-0 top-0 pointer-events-none"
  >
    {#if label}
      <span
        {title}
        class="text-xs"
        class:text-label-600={!focused}
        class:text-primary-500={focused}
      >
        {label}
      </span>
    {/if}
    <div class="w-full h-6 overflow-hidden space-x-1">
      {#each allValueProcessed as value}
        {@const item = allItemProcessed.find((e) => e.value === value)}
        {#if item}
          <span {title} class="px-1 text-xs text-white bg-primary-500 rounded">
            {item.text}
          </span>
        {/if}
      {/each}
    </div>
  </div>
  <select
    bind:this={_element}
    {...elementProps}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100 text-transparent"
    class:pt-6={label}
    class:border-0={!focused}
    class:border-b={label}
    aria-label={label}
    value={null}
    onchange={handleChange}
    onkeydown={handleKeydown}
    onfocus={handleFocus}
    onblur={handleBlur}
  >
    {#each allItemProcessed as item}
      <option class="text-black" value={item.value}>
        {item.text}
      </option>
    {/each}
  </select>
</div>
