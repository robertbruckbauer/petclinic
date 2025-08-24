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

  let element;
  export function focus() {
    element?.focus();
  }

  const primitive = $derived(
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
    if (primitive) {
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
    if (primitive) {
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
    const target = _event.target as HTMLSelectElement;
    if (primitive) {
      value = target.value || null;
    } else {
      const item = target.value ? allItem[target.value] : {};
      if (typeof valueGetter === "function") {
        value = valueGetter(item);
      } else {
        value = item;
      }
    }
    onchange?.(event);
  }

  let _focused = $state(false);
  function handleFocus(_event: FocusEvent) {
    _focused = true;
    onfocus?.(_event);
  }
  function handleBlur(_event: FocusEvent) {
    _focused = false;
    onblur?.(_event);
  }
</script>

<div class="mt-1 relative">
  {#if label}
    <span
      {title}
      class="px-4 pt-2 text-xs absolute left-0 top-0"
      class:text-label-600={!_focused}
      class:text-primary-500={_focused}
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
    class:border-0={!_focused}
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
