<script lang="ts">
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
  function valueMapper(_value: any) {
    if (typeof _value !== "object") {
      return _value;
    } else {
      return _value ? JSON.stringify(_value) : null;
    }
  }

  const allItemProcessed = $derived(allItem.map(itemMapper));
  function itemMapper(_item: any) {
    if (typeof _item !== "object") {
      return {
        value: _item,
        text: _item,
      };
    } else {
      return {
        value: valueMapper(_item.value),
        text: _item.text,
      };
    }
  }

  async function handleChange(_event: Event) {
    const target = _event.target as HTMLSelectElement;
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
    onchange?.(_event);
  }

  const allKeyIgnore = ["ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown"];
  const allKeyDelete = ["Backspace", "Delete"];
  function handleKeydown(_event: KeyboardEvent) {
    if (allKeyIgnore.includes(_event.key)) {
      _event.preventDefault();
      return;
    }
    if (allKeyDelete.includes(_event.key)) {
      _event.preventDefault();
      allValue.pop();
      // Trigger reactivity
      allValue = allValue;
      return;
    }
  }

  let focused = $state(false);
  function handleFocus(_event: FocusEvent) {
    focused = true;
    onfocus?.(_event);
  }
  function handleBlur(_event: FocusEvent) {
    focused = false;
    onblur?.(_event);
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
