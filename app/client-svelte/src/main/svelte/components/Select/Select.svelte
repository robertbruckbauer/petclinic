<script lang="ts">
  let {
    allItem,
    disabled = false,
    label = undefined,
    nullable = false,
    title = undefined,
    value = $bindable(),
    valueGetter = undefined,
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

  const itemIsPrimitive = $derived(
    allItem.slice(0, 1).findIndex((e: any) => typeof e !== "object") !== -1
  );

  function valueMapper(_item: any) {
    if (typeof _item?.value !== "object") {
      return _item?.value;
    } else {
      return _item?.value ? JSON.stringify(_item.value) : null;
    }
  }

  const allItemIndexed = $derived(allItem.map(itemMapper));
  function itemMapper(_value: any, _index: number) {
    if (itemIsPrimitive) {
      return {
        value: _value,
        text: _value,
      };
    } else {
      return {
        value: _index,
        text: _value.text,
      };
    }
  }

  const itemSelected = $derived(itemSelector(value));
  function itemSelector(_value: any) {
    if (itemIsPrimitive) {
      return _value;
    } else {
      if (typeof valueGetter === "function") {
        return allItem.findIndex((e: any) => valueGetter(e) === _value);
      } else {
        return allItem.findIndex(
          (e: any) => valueMapper(e) === valueMapper(_value)
        );
      }
    }
  }

  function handleChange(_event: Event) {
    const _target = _event.target as HTMLSelectElement;
    if (itemIsPrimitive) {
      value = _target.value || null;
    } else {
      const _item = _target.value ? allItem[_target.value] : {};
      if (typeof valueGetter === "function") {
        value = valueGetter(_item);
      } else {
        value = _item;
      }
    }
    onchange?.(_event);
  }
</script>

<div class="mt-1 relative">
  {#if label}
    <span
      {title}
      class="px-4 pt-2 text-xs absolute left-0 top-0"
      class:text-gray-600={!elementIsFocused}
      class:text-indigo-500={elementIsFocused}
    >
      {label}
    </span>
  {/if}
  <select
    bind:this={element}
    {...elementProps}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!elementIsFocused}
    class:border-b={label}
    aria-label={label}
    value={itemSelected}
    onchange={handleChange}
    onfocus={handleFocus}
    onblur={handleBlur}
  >
    {#if nullable}
      <option value={null}>&nbsp;</option>
    {/if}
    {#each allItemIndexed as item}
      <option value={item.value}>{item.text}</option>
    {/each}
  </select>
</div>
