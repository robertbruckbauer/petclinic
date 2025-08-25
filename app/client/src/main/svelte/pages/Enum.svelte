<script>
  import { onMount } from "svelte";
  import Circle from "../components/Spinner";
  import Icon from "../components/Icon";
  import TextField from "../components/TextField";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import EnumEditor from "./EnumEditor.svelte";

  let { art } = $props();

  let loading = $state(true);
  let allItem = $state([]);
  let itemCode = $state(undefined);
  let newItemCode = $derived(
    allItem.length > 0 ? Math.max(...allItem.map((e) => e.code)) + 1 : 1
  );
  let itemEditorCreate = $state(false);
  let itemEditorUpdate = $state(false);
  let filterPrefix = $state("");
  let itemEditorDisabled = $derived(itemEditorCreate || itemEditorUpdate);
  let allItemFiltered = $derived(filterEnum(filterPrefix, allItem));

  onMount(async () => {
    try {
      loading = true;
      await reloadAllItem();
    } catch (_err) {
      console.log(["onMount", _err]);
      toast.push(_err.toString());
    } finally {
      loading = false;
    }
  });

  function onItemClicked(_item) {
    itemCode = _item.code;
  }

  async function onItemRemoveClicked(_item) {
    itemCode = _item.code;
    await removeItem(_item);
  }

  async function onItemEditorCreateClicked() {
    itemEditorCreate = true;
  }

  async function onItemEditorUpdateClicked(_item) {
    itemEditorUpdate = true;
    itemCode = _item.code;
  }

  function filterEnum(_prefix, _allItem) {
    if (!_prefix) return _allItem;
    return _allItem.filter((_item) => {
      if (_item.name.toLowerCase().startsWith(_prefix.toLowerCase())) {
        return true;
      }
      if (_item.text.toLowerCase().startsWith(_prefix.toLowerCase())) {
        return true;
      }
      return false;
    });
  }

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

  function reloadAllItem() {
    return loadAllValue("/api/enum/" + art)
      .then((_json) => {
        const msg = import.meta.env.DEV ? _json : _json.length;
        console.log(["reloadAllItem", art, msg]);
        allItem = _json;
      })
      .catch((_err) => {
        console.log(["reloadAllItem", art, _err]);
        allItem = [];
        toast.push(_err.toString());
      });
  }

  function removeItem(_item) {
    const _text = _item.name;
    const _hint = _text.length > 20 ? _text.substring(0, 20) + "..." : _text;
    if (!confirm("Enum '" + _hint + "' wirklich löschen?")) return;
    return removeValue("/api/enum/" + art + "/" + _item.code)
      .then((_json) => {
        console.log(["removeItem", _item, _json]);
        onRemoveItem(_json);
      })
      .catch((_err) => {
        console.log(["removeItem", _item, _err]);
        toast.push(_err.toString());
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
                oncreate={(item) => onCreateItem(item)}
                {art}
                code={newItemCode}
              />
            </td>
          </tr>
        {/if}
        {#each allItemFiltered as item, i}
          <tr
            onclick={() => onItemClicked(item)}
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
                  onupdate={(item) => onUpdateItem(item)}
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
