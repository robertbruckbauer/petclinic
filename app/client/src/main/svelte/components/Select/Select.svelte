<script lang="ts">
  import { tick } from "svelte";

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

  let _element;
  export function focus() {
    _element?.focus();
  }

  const _primitive = $derived(
    allItem.slice(0, 1).findIndex((e: any) => typeof e !== "object") !== -1
  );

  function itemMapper(e: any, i: number) {
    if (_primitive) {
      return {
        value: e,
        text: e,
      };
    } else {
      return {
        value: i,
        text: e.text,
      };
    }
  }

  const _allItemIndexed = $derived(allItem.map(itemMapper));

  function itemSelected(v: any) {
    if (_primitive) {
      return v;
    } else {
      if (typeof valueGetter === "function") {
        return allItem.findIndex((e: any) => valueGetter(e) === v);
      } else {
        return allItem.findIndex((e: any) => itemString(e) === itemString(v));
      }
    }
  }

  const _itemSelected = $derived(itemSelected(value));

  function itemString(item: any) {
    if (typeof item?.value !== "object") {
      return item?.value;
    } else {
      return item?.value ? JSON.stringify(item.value) : null;
    }
  }

  async function handleChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    if (_primitive) {
      value = target.value || null;
    } else {
      const item = target.value ? allItem[target.value] : {};
      if (typeof valueGetter === "function") {
        value = valueGetter(item);
      } else {
        value = item;
      }
    }
    await tick();
    onchange?.(event);
  }

  let _focused = $state(false);
  async function handleFocus(event: FocusEvent) {
    _focused = true;
    await tick();
    onfocus?.(event);
  }
  async function handleBlur(event: FocusEvent) {
    _focused = false;
    await tick();
    onblur?.(event);
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
    bind:this={_element}
    {...elementProps}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!_focused}
    class:border-b={label}
    aria-label={label}
    value={_itemSelected}
    onchange={handleChange}
    onfocus={handleFocus}
    onblur={handleBlur}
  >
    {#if nullable}
      <option value={null}>&nbsp;</option>
    {/if}
    {#each _allItemIndexed as item}
      <option value={item.value}>{item.text}</option>
    {/each}
  </select>
</div>
