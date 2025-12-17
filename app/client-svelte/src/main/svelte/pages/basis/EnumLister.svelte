<script lang="ts">
  import { onMount, untrack } from "svelte";
  import { take } from "rxjs";
  import { toast } from "../../controls/Toast";
  import { EnumService, filterByCriteria } from "../../services/enum.service";
  import type { EnumItem } from "../../types/enum.type";
  import EnumEditor from "./EnumEditor.svelte";

  interface Props {
    art: "species" | "skill";
  }

  let { art }: Props = $props();

  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      loadAllItem();
    } catch (err) {
      toast.push(err);
    } finally {
      loading = false;
    }
  });

  let itemCode = $state(0);
  function onItemClicked(_item: EnumItem) {
    itemCode = _item.code;
  }
  function onItemRemoveClicked(_item: EnumItem) {
    itemCode = _item.code;
    removeItem(_item);
  }

  let itemEditorCreate = $state(false);
  function onItemEditorCreateClicked() {
    itemEditorCreate = true;
    itemEditorUpdate = false;
  }

  let itemEditorUpdate = $state(false);
  function onItemEditorUpdateClicked(_item: EnumItem) {
    itemEditorCreate = false;
    itemEditorUpdate = true;
    itemCode = _item.code;
  }

  const itemFilterDisabled = $derived(itemEditorCreate || itemEditorUpdate);

  const itemEditorDisabled = $derived(itemFilterDisabled);

  let allItem: EnumItem[] = $state([]);
  function afterCreateItem(_item: EnumItem) {
    allItem = [_item, ...allItem];
  }
  function afterUpdateItem(_item: EnumItem) {
    allItem = allItem.map((e) => (e.code === _item.code ? _item : e));
  }
  function afterRemoveItem(_item: EnumItem) {
    allItem = allItem.filter((e) => e.code !== _item.code);
  }

  const newItemCode = $derived(Math.max(...allItem.map((e) => e.code)) + 1);

  let itemFilter = $state("");
  function onItemFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllItem();
    } catch (err) {
      toast.push(err);
    } finally {
      loading = false;
    }
  }

  // do not update on criteria change
  const allItemFiltered = $derived.by(() => {
    return allItem.filter(filterByCriteria(untrack(() => itemFilter)));
  });

  const enumService = new EnumService();

  function loadAllItem() {
    enumService
      .loadAllEnum(art)
      .pipe(take(1))
      .subscribe({
        next: (json) => {
          allItem = json;
        },
        error: (err) => {
          toast.push(err);
        },
      });
  }

  function removeItem(_item: EnumItem) {
    const text = _item.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete enum '" + hint + "' permanently?")) return;
    enumService
      .removeEnum(art, _item.code)
      .pipe(take(1))
      .subscribe({
        next: (json) => {
          afterRemoveItem(json);
        },
        error: (err) => {
          toast.push(err);
        },
      });
  }
</script>

<h1>{art.toUpperCase()}</h1>

<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onItemFilterClicked}>
    <div class="flex flex-row gap-2 items-center pb-2 pr-2">
      <input
        bind:value={itemFilter}
        aria-label="Filter"
        type="text"
        class="input w-full"
        readonly={itemFilterDisabled}
        placeholder="Enter filter critria"
      />
      <button
        type="submit"
        title="Filter items"
        class="btn btn-circle btn-outline"
        disabled={itemEditorDisabled}
      >
        <span class="material-icons">search</span>
      </button>
    </div>
  </form>
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {:else}
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-200">
          <th class="px-2 py-3 text-left table-cell w-1/4 sm:w-1/8">
            <span class="text-gray-600">Code</span>
          </th>
          <th class="px-2 py-3 text-left table-cell w-3/4 sm:w-3/8">
            <span class="text-gray-600">Name</span>
          </th>
          <th class="px-2 py-3 text-left hidden sm:table-cell sm:w-full">
            <span class="text-gray-600">Text</span>
          </th>
          <th class="px-2 py-3 text-right table-cell">
            <button
              title="Add a new item"
              class="btn btn-circle btn-outline"
              onclick={onItemEditorCreateClicked}
              disabled={itemEditorDisabled}
            >
              <span class="material-icons">add</span>
            </button>
          </th>
        </tr>
      </thead>
      <tbody>
        {#if itemEditorCreate}
          {@const item = { version: 0, code: newItemCode, name: "", text: "" }}
          <tr>
            <td class="border-l-4 px-2" colspan="4">
              <EnumEditor
                oncreate={afterCreateItem}
                bind:visible={itemEditorCreate}
                {art}
                {item}
              />
            </td>
          </tr>
        {/if}
        {#each allItemFiltered as item, i}
          <tr
            title={item.text}
            onclick={() => onItemClicked(item)}
            class:border-l-2={itemCode === item.code}
            class:bg-gray-100={i % 2 === 1}
          >
            <td class="px-2 py-3 text-left table-cell">
              <span>{item.code}</span>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <span>{item.name}</span>
            </td>
            <td class="px-2 py-3 text-left hidden sm:table-cell">
              <span>{item.text}</span>
            </td>
            <td class="px-2 py-3 text-right table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-2 items-center gap-1 w-max"
              >
                <button
                  title="Delete an item"
                  class="btn btn-circle btn-outline"
                  onclick={() => onItemRemoveClicked(item)}
                  disabled={itemEditorDisabled}
                >
                  <span class="material-icons">delete</span>
                </button>
                <button
                  title="Edit an item"
                  class="btn btn-circle btn-outline"
                  onclick={() => onItemEditorUpdateClicked(item)}
                  disabled={itemEditorDisabled}
                >
                  <span class="material-icons">edit</span>
                </button>
              </div>
            </td>
          </tr>
          {#if itemEditorUpdate && itemCode === item.code}
            <tr>
              <td class="border-l-4 px-2" colspan="4">
                <EnumEditor
                  onupdate={afterUpdateItem}
                  bind:visible={itemEditorUpdate}
                  {art}
                  {item}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="4">No items</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
