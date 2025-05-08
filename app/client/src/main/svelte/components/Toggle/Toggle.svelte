<script>
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["allItem", "allValue", "disabled", "label", "title"],
    $$props
  );
  export let allItem;
  export let allValue;
  export let disabled = false;
  export let label = undefined;
  export let title = undefined;
  let focused;
  let element;
  export function focus() {
    element.focus();
  }

  $: allValueProcessed = allValue.map(processValue);
  function processValue(value) {
    if (typeof value !== "object") {
      return value;
    } else {
      return value ? JSON.stringify(value) : null;
    }
  }

  $: allItemProcessed = allItem.map(processItem);
  function processItem(item) {
    if (typeof item !== "object") {
      return {
        value: item,
        text: item,
      };
    } else {
      return {
        value: processValue(item.value),
        text: item.text,
      };
    }
  }

  function onChange({ target }) {
    let item = allItem[target.selectedIndex];
    let itemProcessed = allItemProcessed[target.selectedIndex];
    let itemIndex = allValueProcessed.findIndex(
      (e) => e === itemProcessed.value
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
    // Assign for reactivity
    allValue = allValue;
    dispatch("change", allValue);
  }

  function onKey(e) {
    if (["ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown"].includes(e.key)) {
      e.preventDefault();
      return;
    }
    if (["Backspace", "Delete"].includes(e.key)) {
      allValue.pop();
      // Assign for reactivity
      allValue = allValue;
      return;
    }
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
    bind:this={element}
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100 text-transparent"
    class:pt-6={label}
    class:border-0={!focused}
    class:border-b={label}
    aria-label={label}
    value="null"
    on:change={onChange}
    on:input
    on:keydown={onKey}
    on:keypress
    on:keyup
    on:keyup
    on:click
    on:focus={() => (focused = true)}
    on:focus
    on:blur={() => (focused = false)}
    on:blur
  >
    {#each allItemProcessed as item}
      <option class="text-black" value={item.value}>
        {item.text}
      </option>
    {/each}
  </select>
</div>
