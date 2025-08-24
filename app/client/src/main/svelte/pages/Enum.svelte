<script>
  import { onMount } from "svelte";
  import Icon from "../components/Icon";
  import TextField from "../components/TextField";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { updateValue } from "../utils/rest.js";
  import EnumEditor from "./EnumEditor.svelte";

  export let art;

  let allItem = [];
  let itemCode = undefined;
  function onItemClicked(item) {
    itemCode = item.code;
  }

  let itemEditorCreate = false;
  let itemEditorUpdate = false;
  $: itemEditorDisabled = itemEditorCreate || itemEditorUpdate;
  function itemEditorCreateClicked() {
    itemEditorCreate = true;
  }
  function itemEditorUpdateClicked(code) {
    itemCode = code;
    itemEditorUpdate = code;
    itemEditorUpdate = true;
  }

  onMount(async () => {
    reloadAllItem();
  });

  let filterPrefix = "";
  $: allItemFiltered = filterEnum(filterPrefix, allItem);
  function filterEnum(prefix, allValue) {
    if (!filterPrefix) return allValue;
    return allValue.filter((e) => {
      if (e.name.toLowerCase().startsWith(prefix.toLowerCase())) {
        return true;
      }
      if (e.text.toLowerCase().startsWith(prefix.toLowerCase())) {
        return true;
      }
      return false;
    });
  }

  function reloadAllItem() {
    loadAllValue("/api/enum/" + art)
      .then((json) => {
        console.log(["reloadAllItem", json]);
        allItem = json;
      })
      .catch((err) => {
        console.log(["reloadAllItem", err]);
        toast.push(err.toString());
      });
  }
  function updateItem(item) {
    updateValue("/api/enum/" + art + "/" + item.code, item)
      .then(() => {
        reloadAllItem();
      })
      .catch((err) => {
        console.log(["updateItem", err]);
        toast.push(err.toString());
      });
  }
</script>

<h1>{art.toUpperCase()}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <div class="flex-grow">
    <TextField
      bind:value={filterPrefix}
      disabled={itemEditorDisabled}
      label="Filter"
      placeholder="Insert a criteria"
    />
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-100">
          <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
            <span class="text-gray-600">Code</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
            <span class="text-gray-600">Name</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/2">
            <span class="text-gray-600">Text</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 w-16">
            <Icon
              onclick={() => itemEditorCreateClicked()}
              disabled={itemEditorDisabled}
              name="edit"
              outlined
            />
          </th>
        </tr>
      </thead>
      <tbody>
        {#if itemEditorCreate}
          <tr>
            <td class="px-4" colspan="4">
              <EnumEditor
                bind:visible={itemEditorCreate}
                on:create={(e) => reloadAllItem()}
                {art}
                code={allItem.length}
              />
            </td><td> </td></tr
          >
        {/if}
        {#each allItemFiltered as item, i}
          <tr
            on:click={(e) => onItemClicked(item)}
            title={item.text}
            class:ring={itemCode === item.code}
          >
            <td class="px-2 py-3 text-left">
              <span>{item.code}</span>
            </td>
            <td class="px-2 py-3 text-left">
              <span>{item.name}</span>
            </td>
            <td class="px-2 py-3 text-left">
              <span>{item.text}</span>
            </td>
            <td class="px-2 py-3">
              <Icon
                onclick={() => itemEditorUpdateClicked(item)}
                disabled={itemEditorDisabled}
                name="edit"
                outlined
              />
            </td>
          </tr>
          {#if itemEditorUpdate && itemCode === item.code}
            <tr>
              <td class="px-4" colspan="4">
                <EnumEditor
                  bind:visible={itemEditorUpdate}
                  on:update={(e) => reloadAllItem()}
                  on:remove={(e) => reloadAllItem()}
                  {art}
                  code={item.code}
                  {item}
                />
              </td><td> </td></tr
            >
          {/if}
        {:else}
          <tr>
            <td class="px-2 py-3" colspan="4"> No items </td>
          </tr>
        {/each}
      </tbody>
    </table>
  </div>
</div>
