<script>
  import { onMount } from "svelte";
  import Circle from "../components/Spinner";
  import Icon from "../components/Icon";
  import TextField from "../components/TextField";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import EnumEditor from "./EnumEditor.svelte";

  export let art;

  let loading = true;
  onMount(async () => {
    try {
      loading = true;
      await reloadAllItem();
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    } finally {
      loading = false;
    }
  });

  $: newCode = Math.max(...allItem.map((e) => e.code)) + 1;
  let itemCode = undefined;
  function onItemClicked(item) {
    itemCode = item.code;
  }
  async function onItemRemoveClicked(item) {
    itemCode = item.code;
    await removeItem(item);
  }
  let itemEditorCreate = false;
  async function onItemEditorCreateClicked() {
    itemEditorCreate = true;
  }
  let itemEditorUpdate = false;
  async function onItemEditorUpdateClicked(item) {
    itemEditorUpdate = true;
    itemCode = item.code;
  }
  $: itemEditorDisabled = itemEditorCreate || itemEditorUpdate;

  let filterPrefix = "";
  $: allItemFiltered = filterEnum(filterPrefix, allItem);
  function filterEnum(_prefix, _allItem) {
    if (!_prefix) return _allItem;
    return _allItem.filter((e) => {
      if (e.name.toLowerCase().startsWith(_prefix.toLowerCase())) {
        return true;
      }
      if (e.text.toLowerCase().startsWith(_prefix.toLowerCase())) {
        return true;
      }
      return false;
    });
  }

  let allItem = [];
  function onCreateItem(item) {
    allItem = allItem.toSpliced(0, 0, item);
  }
  function onUpdateItem(item) {
    let index = allItem.findIndex((e) => e.code === item.code);
    if (index > -1) allItem = allItem.toSpliced(index, 1, item);
  }
  function onRemoveItem(item) {
    let index = allItem.findIndex((e) => e.code === item.code);
    if (index > -1) allItem = allItem.toSpliced(index, 1);
  }
  function reloadAllItem() {
    return loadAllValue("/api/enum/" + art)
      .then((json) => {
        const msg = import.meta.env.DEV ? json : json.length;
        console.log(["reloadAllItem", art, msg]);
        allItem = json;
      })
      .catch((err) => {
        console.log(["reloadAllItem", art, err]);
        allItem = [];
        toast.push(err.toString());
      });
  }

  function removeItem(item) {
    const text = item.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Enum '" + hint + "' wirklich löschen?")) return;
    return removeValue("/api/enum/" + art + "/" + item.code)
      .then((json) => {
        console.log(["removeItem", item, json]);
        onRemoveItem(json);
      })
      .catch((err) => {
        console.log(["removeItem", item, err]);
        toast.push(err.toString());
      });
  }
</script>

<h1 title="Liste der Werte, ggfs. gefiltert, jedes Element editierbar">
  {art.toUpperCase()}
</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <div class="flex flex-row gap-1 items-center">
    <div class="w-full">
      <TextField
        bind:value={filterPrefix}
        label="Filter"
        placeholder="Bitte Filterkriterien eingeben"
      />
    </div>
  </div>
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
              title="Wert hinzufügen"
              name="add"
              outlined
            />
          </th>
        </tr>
      </thead>
      <tbody>
        {#if itemEditorCreate}
          <tr>
            <td class="px-2" colspan="4">
              <EnumEditor
                bind:visible={itemEditorCreate}
                on:create={(e) => onCreateItem(e.detail)}
                {art}
                code={newCode}
              />
            </td>
          </tr>
        {/if}
        {#each allItemFiltered as item, i}
          <tr
            on:click={(e) => onItemClicked(item)}
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
                  title="Wert löschen"
                  name="delete"
                  outlined
                />
                <Icon
                  onclick={() => onItemEditorUpdateClicked(item)}
                  disabled={itemEditorDisabled}
                  title="Wert bearbeiten"
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
                  on:update={(e) => onUpdateItem(e.detail)}
                  {art}
                  code={item.code}
                  {item}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="4">Keine Werte</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
