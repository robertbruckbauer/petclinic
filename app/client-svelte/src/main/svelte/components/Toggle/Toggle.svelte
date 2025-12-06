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

  let element: HTMLSelectElement;
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

  function handleChange(_event: Event) {
    const _target = _event.target as HTMLSelectElement;
    let _item = allItem[_target.selectedIndex];
    let _itemProcessed = allItemProcessed[_target.selectedIndex];
    let _itemIndex = allValueProcessed.findIndex(
      (e: any) => e === _itemProcessed.value
    );
    if (_itemIndex === -1) {
      if (typeof _item !== "object") {
        allValue = allValue.toSpliced(0, 0, _item);
      } else {
        allValue = allValue.toSpliced(0, 0, _item.value);
      }
    } else {
      allValue = allValue.toSpliced(_itemIndex, 1);
    }
    // The value is the whole array as a csv string
    // The value always differs from the element value
    element.value = JSON.stringify(allValue);
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
</script>

<div class="mt-1 relative">
  <div
    class="flex flex-col px-4 pt-2 absolute left-0 top-0 pointer-events-none"
  >
    {#if label}
      <span
        {title}
        class="text-xs"
        class:text-gray-600={!elementIsFocused}
        class:text-indigo-500={elementIsFocused}
      >
        {label}
      </span>
    {/if}
    <div class="w-full h-6 overflow-hidden space-x-1">
      {#each allValueProcessed as value}
        {@const item = allItemProcessed.find((e) => e.value === value)}
        {#if item}
          <span
            title={item.text}
            class="px-1 text-xs text-white bg-indigo-500 rounded"
          >
            {item.text}
          </span>
        {/if}
      {/each}
    </div>
  </div>
  <select
    bind:this={element}
    {...elementProps}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100 text-transparent"
    class:pt-6={label}
    class:border-0={!elementIsFocused}
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
