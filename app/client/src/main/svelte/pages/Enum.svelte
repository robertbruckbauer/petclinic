<script>
  import { onMount } from "svelte";
  import Icon from "../components/Icon";
  import TextField from "../components/TextField";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import Circle from "../components/Spinner";
  import EnumEditor from "./EnumEditor.svelte";

  let { art } = $props();

  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      loadAllItem();
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    } finally {
      loading = false;
    }
  });

  let newItemCode = $state(0);
  let itemCode = $state();
  function onItemClicked(_item) {
    itemCode = _item.code;
  }
  function onItemRemoveClicked(_item) {
    itemCode = _item.code;
    removeItem(_item);
  }

  let itemEditorCreate = $state(false);
  function onItemEditorCreateClicked() {
    itemEditorCreate = true;
    itemEditorUpdate = false;
  }

  let itemEditorUpdate = $state(false);
  function onItemEditorUpdateClicked(_item) {
    itemEditorCreate = false;
    itemEditorUpdate = true;
    itemCode = _item.code;
  }

  let itemEditorDisabled = $derived(itemEditorCreate || itemEditorUpdate);

  let itemFilter = $state("");
  function onItemFilterClicked(_event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllItem();
    } finally {
      loading = false;
    }
  }

  let allItem = $state([]);
  function onCreateItem(_item) {
    allItem = allItem.toSpliced(0, 0, _item);
  }
  function onUpdateItem(_item) {
    let index = allItem.findIndex((e) => e.code === _item.code);
    if (index > -1) allItem = allItem.toSpliced(index, 1, _item);
  }
  function onRemoveItem(_item) {
    let index = allItem.findIndex((e) => e.code === _item.code);
    if (index > -1) allItem = allItem.toSpliced(index, 1);
  }

  function loadAllItem() {
    loadAllValue("/api/enum/" + art)
      .then((json) => {
        const msg = import.meta.env.DEV ? json : json.length;
        console.log(["loadAllItem", art, msg]);
        newItemCode = Math.max(...json.map((e) => e.code)) + 1;
        allItem = json.filter((e) => {
          if (!itemFilter) return true;
          if (e.name.toLowerCase().startsWith(itemFilter.toLowerCase())) {
            return true;
          }
          if (e.text.toLowerCase().startsWith(itemFilter.toLowerCase())) {
            return true;
          }
        });
      })
      .catch((err) => {
        console.log(["loadAllItem", art, err]);
        toast.push(err.toString());
      });
  }

  function removeItem(_item) {
    const text = _item.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Enum '" + hint + "' wirklich löschen?")) return;
    removeValue("/api/enum/" + art + "/" + _item.code)
      .then((json) => {
        console.log(["removeItem", _item, json]);
        onRemoveItem(json);
      })
      .catch((err) => {
        console.log(["removeItem", _item, err]);
        toast.push(err.toString());
      });
  }
</script>

<h1>{art.toUpperCase()}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onItemFilterClicked}>
    <div class="flex flex-row gap-1 items-center pr-2">
      <div class="w-full">
        <TextField
          bind:value={itemFilter}
          label="Filter"
          placeholder="Bitte Filterkriterien eingeben"
        />
      </div>
      <div class="w-min">
        <Icon type="submit" name="search" outlined />
      </div>
    </div>
  </form>
  {#if loading}
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
    </div>
  {:else}
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-title-100">
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-title-600">Code</span>
          </th>
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-title-600">Name</span>
          </th>
          <th class="px-2 py-3 text-left w-1/2 table-cell">
            <span class="text-title-600">Text</span>
          </th>
          <th class="px-2 py-3 text-right w-0 table-cell">
            <Icon
              onclick={() => onItemEditorCreateClicked()}
              disabled={itemEditorDisabled}
              title="Add a new item"
              name="add"
              outlined
            />
          </th>
        </tr>
      </thead>
      <tbody>
        {#if itemEditorCreate}
          <tr>
            <td class="border-l-4 px-2" colspan="4">
              <EnumEditor
                bind:visible={itemEditorCreate}
                oncreate={onCreateItem}
                {art}
                code={newItemCode}
              />
            </td>
          </tr>
        {/if}
        {#each allItem as item, i}
          <tr
            onclick={(e) => onItemClicked(item)}
            title={item.text}
            class:border-l-2={itemCode === item.code}
            class:bg-gray-100={i % 2 === 1}
          >
            <td class="px-2 py-3 text-left table-cell">
              <span>{item.code}</span>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <span>{item.name}</span>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <span>{item.text}</span>
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-2 items-center gap-1 w-max"
              >
                <Icon
                  onclick={() => onItemRemoveClicked(item)}
                  disabled={itemEditorDisabled}
                  title="Delete an item"
                  name="delete"
                  outlined
                />
                <Icon
                  onclick={() => onItemEditorUpdateClicked(item)}
                  disabled={itemEditorDisabled}
                  title="Edit an item"
                  name="edit"
                  outlined
                />
              </div>
            </td>
          </tr>
          {#if itemEditorUpdate && itemCode === item.code}
            <tr>
              <td class="border-l-4 px-2" colspan="4">
                <EnumEditor
                  bind:visible={itemEditorUpdate}
                  onupdate={onUpdateItem}
                  {art}
                  code={item.code}
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
