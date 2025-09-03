<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue, updateValue } from "../utils/rest.js";
  import Button from "../components/Button";
  import TextField from "../components/TextField";
  import TextArea from "../components/TextArea";

  let {
    visible = $bindable(false),
    autofocus = true,
    autoscroll = true,
    art,
    code,
    item = undefined,
    oncreate = undefined,
    onupdate = undefined,
  } = $props();

  let focusOn;
  let bottomDiv;
  let clicked = $state(false);
  let showUpdate = $state(false);
  let newItem = $state({
    code: code,
    name: "",
    text: "",
  });

  $effect(() => {
    if (item) {
      showUpdate = true;
      newItem.name = item.name;
      newItem.text = item.text;
    }
  });

  onMount(async () => {
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
    console.log(["onMount", autofocus, autoscroll]);
  });

  async function onSubmit() {
    try {
      clicked = true;
      if (showUpdate) {
        await updateItem();
      } else {
        await createItem();
      }
    } finally {
      clicked = false;
    }
  }

  function onCancel() {
    visible = false;
  }

  function createItem() {
    createValue("/api/enum/" + art, newItem)
      .then((_json) => {
        console.log(["createItem", newItem, _json]);
        visible = false;
        oncreate?.(_json);
      })
      .catch((_err) => {
        console.log(["createItem", newItem, _err]);
        toast.push(_err.toString());
      });
  }

  function updateItem() {
    updateValue("/api/enum/" + art + "/" + newItem.code, newItem)
      .then((_json) => {
        console.log(["updateItem", newItem, _json]);
        visible = false;
        onupdate?.(_json);
      })
      .catch((_err) => {
        console.log(["updateItem", newItem, _err]);
        toast.push(_err.toString());
      });
  }
</script>

<form
  onsubmit={(e) => {
    e.preventDefault();
    onSubmit();
  }}
  onreset={(e) => {
    e.preventDefault();
    onCancel();
  }}
>
  <div class="flex flex-col gap-1">
    <div class="w-full flex flex-row gap-1 items-baseline">
      <div class="w-1/6">
        <TextField
          bind:value={newItem.code}
          required
          type="number"
          label="Code"
          placeholder="Bitte einen Code eingeben"
          disabled={showUpdate}
        />
      </div>
      <div class="w-full">
        <TextField
          bind:this={focusOn}
          bind:value={newItem.name}
          required
          label="Name"
          placeholder="Bitte einen Namen eingeben"
        />
      </div>
    </div>
    <div class="w-full">
      <TextArea
        bind:value={newItem.text}
        required
        label="Text"
        placeholder="Bitte einen Text eingeben"
      />
    </div>
  </div>
  <div class="py-4 flex flex-row gap-1 items-baseline">
    <div class="flex-initial">
      <Button type="submit">Ok</Button>
    </div>
    <div class="flex-initial">
      <Button type="reset">Abbrechen</Button>
    </div>
  </div>
</form>

<div class="h-0" bind:this={bottomDiv}>&nbsp;</div>

{#if import.meta.env.DEV}
  <details tabindex="-1">
    <summary>JSON</summary>
    <pre>{JSON.stringify(newItem, null, 2)}</pre>
  </details>
{/if}
