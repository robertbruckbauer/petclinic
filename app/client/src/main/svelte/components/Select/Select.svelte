<script>
  import { createEventDispatcher, tick } from "svelte";
  const dispatch = createEventDispatcher();
  import filterProps from "../filterProps.js";
  const props = filterProps(
    [
      "allItem",
      "disabled",
      "label",
      "nullable",
      "title",
      "value",
      "valueGetter",
    ],
    $$props
  );
  export let allItem;
  export let disabled = false;
  export let label = undefined;
  export let nullable = false;
  export let title = undefined;
  export let value;
  export let valueGetter = undefined;

  let _focused;
  let _element;
  export function focus() {
    _element.focus();
  }

  $: _primitive =
    allItem.slice(0, 1).findIndex((e) => typeof e !== "object") !== -1;

  $: _allItemIndexed = allItem.map((e, i) => itemMapper(e, i));
  function itemMapper(e, i) {
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

  $: _itemSelected = itemSelected(value);
  function itemSelected(v) {
    if (_primitive) {
      return v;
    } else {
      if (typeof valueGetter === "function") {
        return allItem.findIndex((e) => valueGetter(e) === v);
      } else {
        return allItem.findIndex((e) => itemString(e) === itemString(v));
      }
    }
  }
  function itemString(item) {
    if (typeof item?.value !== "object") {
      return item?.value;
    } else {
      return item?.value ? JSON.stringify(item.value) : null;
    }
  }

  async function onChange({ target }) {
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
    await dispatch("change", value);
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
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!_focused}
    class:border-b={label}
    aria-label={label}
    value={_itemSelected}
    on:change={onChange}
    on:input
    on:keydown
    on:keypress
    on:keyup
    on:click
    on:focus={() => (_focused = true)}
    on:focus
    on:blur={() => (_focused = false)}
    on:blur
  >
    {#if nullable}
      <option value={null}>&nbsp;</option>
    {/if}
    {#each _allItemIndexed as item}
      <option value={item.value}>{item.text}</option>
    {/each}
  </select>
</div>
