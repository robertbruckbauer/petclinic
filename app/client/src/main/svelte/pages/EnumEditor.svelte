<script>
  import { createEventDispatcher } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue } from "../utils/rest.js";
  import { updateValue } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import Button from "../components/Button";
  import TextField from "../components/TextField";
  import TextArea from "../components/TextArea";

  export let visible = false;
  export let item = undefined;
  export let art;
  export let code;

  let showUpdate;
  let showRemove;
  let newItem = {
    code: code,
    name: "",
    text: "",
  };

  $: if (item) onChangeItem();
  function onChangeItem() {
    showUpdate = true;
    showRemove = false;
    newItem = {
      code: code,
      name: item.name,
      text: item.text,
    };
    console.log(["onChangeItem", newItem]);
  }

  $: disabled = !newItem.name || !newItem.text;

  const dispatch = createEventDispatcher();
  function onCreateItem() {
    createValue("/api/enum/" + art, newItem)
      .then((json) => {
        console.log(["onCreateItem", newItem, json]);
        visible = false;
        dispatch("create", newItem);
      })
      .catch((err) => {
        console.log(["onCreateItem", newItem, err]);
        toast.push(err.toString());
      });
  }
  function onUpdateItem() {
    updateValue("/api/enum/" + art + "/" + newItem.code, newItem)
      .then((json) => {
        console.log(["onUpdateItem", newItem, json]);
        visible = false;
        dispatch("update", newItem);
      })
      .catch((err) => {
        console.log(["onUpdateItem", newItem, err]);
        toast.push(err.toString());
      });
  }
  function onRemoveItem() {
    const text = newItem.text;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Item '" + hint + "' wirklich löschen?")) return;
    removeValue("/api/enum/" + art + "/" + newItem.code)
      .then(() => {
        console.log(["onRemoveItem", newItem]);
        visible = false;
        dispatch("remove", newItem);
      })
      .catch((err) => {
        console.log(["onRemoveItem", newItem, err]);
        toast.push(err.toString());
      });
  }
  function onCancel() {
    visible = false;
  }
</script>

<div class="flex flex-col gap-1">
  <div class="w-full flex flex-row gap-1 items-baseline">
    <div class="w-1/6">
      <TextField
        bind:value={newItem.code}
        type="number"
        label="Code"
        placeholder="Bitte den Code eingeben"
        disabled={showUpdate}
      />
    </div>
    <div class="w-full">
      <TextField
        bind:value={newItem.name}
        label="Name"
        placeholder="Bitte den Namen eingeben"
      />
    </div>
  </div>
  <div class="w-full">
    <TextArea
      bind:value={newItem.text}
      label="Text"
      placeholder="Bitte den Text eingeben"
    />
  </div>
</div>

<div class="py-4">
  {#if showUpdate}
    <Button on:click={() => onUpdateItem()} {disabled}>Ok</Button>
  {:else}
    <Button on:click={() => onCreateItem()} {disabled}>Ok</Button>
  {/if}
  {#if showRemove}
    <Button on:click={() => onRemoveItem()}>Löschen</Button>
  {/if}
  <Button on:click={() => onCancel()}>Abbrechen</Button>
</div>

<details tabindex="-1">
  <summary>JSON</summary>
  <pre>{JSON.stringify(newItem, null, 2)}</pre>
</details>
