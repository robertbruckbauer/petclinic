<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'disabled',
    'label',
    'value',
    'title'
  ], $$props);
  export let allItem = [];
  export let allValue;
  export let disabled = false;
  export let label;
  export let title = undefined;
  let focused;
  
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
        text: item 
      };
    } else {
      return {
        value: processValue(item.value),
        text: item.text
      }
    }
  }

  function onChange({ target }) {
    let item = allItem[target.selectedIndex];
    let itemProcessed = allItemProcessed[target.selectedIndex];
    let itemIndex = allValueProcessed.findIndex(e => e === itemProcessed.value);
    if (itemIndex === -1) {      
      if (typeof item !== "object") {
        allValue.push(item);
      } else {
        allValue.push(item.value);
      }
      allValueProcessed.push(itemProcessed.value);
    } else {
      allValue.splice(itemIndex, 1);
      allValueProcessed.splice(itemIndex, 1);
    }
    // Assign for reactivity
    allValue = allValue;
  }

  function onKey(e) {
    // Avoid on:change event
    if (e.key === 'ArrowLeft') e.preventDefault();
    if (e.key === 'ArrowRight') e.preventDefault();
  }
</script>

<div
  class="mt-2 mb-6 relative"
>
  <div class="flex flex-col pb-2 px-4 pt-2 absolute left-0 top-0 pointer-events-none">
    <span
      {title}
      class="text-xs"
      class:text-gray-600={!focused}
      class:text-primary-500={focused}
    >
      {label}
    </span>
    <div class="w-full h-6 overflow-hidden space-x-1">
      {#each allItemProcessed as item}
      {#if allValueProcessed.findIndex(e => e === item.value) !== -1}
        <span
          {title}
          class="p-1 text-xs text-white bg-primary-500 rounded"
        >
          {item.text}
        </span>
        {/if}
      {/each}
    </div>
  </div>
  <select
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full pb-2 px-4 pt-6 text-black bg-gray-100 text-transparent"
    class:border-0={!focused}
    aria-label={label}
    value=null
    on:change={onChange}
    on:change
    on:input
    on:keydown={onKey}
    on:keydown
    on:keypress={onKey}
    on:keypress
    on:keyup={onKey}
    on:keyup
    on:click
    on:focus={() => focused = true}
    on:focus
    on:blur={() => focused = false}
    on:blur
  >
    {#each allItemProcessed as item}
    <option class="text-black" value={item.value}>
      {item.text}
    </option>
    {/each}  
  </select>
  <div class="w-full bg-gray-600 absolute left-0 bottom-0">
    <div 
      class="mx-auto w-0" 
      style="height: 1px; transition: width 0.2s ease 0s;"
    />
  </div>
</div>
