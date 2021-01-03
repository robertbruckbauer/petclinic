<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'disabled',
    'label',
    'value',
    'title'
  ], $$props);
  export let allItem = [];
  export let disabled = false;
  export let label;
  export let nullable = false;
  export let value;
  export let valueItem = undefined;
  export let valueNull = '';
  export let title = undefined;
  let focused;
  
  $: valueProcessed = processValue(value);
  function processValue(e) {
    if (typeof e !== "object") {
      return e;
    } else {
      return e ? JSON.stringify(e) : null;
    }
  }

  $: allItemProcessed = allItem.map(processItem);
  function processItem(e) {
    if (typeof e !== "object") {
      return {
        value: e, 
        text: e 
      };
    } else {
      return {
        value: processValue(e.value),
        text: e.text
      }
    }
  }

  function onChange({ target }) {
    valueItem = allItem[target.selectedIndex-1];
    if (typeof valueItem !== "object") {
      value = valueItem;
    } else {
      value = valueItem.value;
    }
  }
</script>

<div
  class="mt-2 mb-6 relative"
>
  {#if label}  
  <span
    {title}
    class="pb-2 px-4 pt-2 text-xs absolute left-0 top-0"
    class:text-gray-600={!focused}
    class:text-primary-500={focused}
  >
    {label}
  </span>
  {/if}
  <select
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full pb-2 px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!focused}
    aria-label={label}
    value={valueProcessed}
    on:change={onChange}
    on:change
    on:input
    on:keydown
    on:keypress
    on:keyup
    on:click
    on:focus={() => focused = true}
    on:focus
    on:blur={() => focused = false}
    on:blur
  >
    <option disabled={!nullable} value={null}>{valueNull}</option>
    {#each allItemProcessed as item}
    <option value={item.value}>
      {item.text}
    </option>
    {/each}  
  </select>
  {#if label}
  <div class="w-full bg-gray-600 absolute left-0 bottom-0">
    <div 
      class="mx-auto w-0" 
      style="height: 1px; transition: width 0.2s ease 0s;"
    />
  </div>
  {/if}
</div>
