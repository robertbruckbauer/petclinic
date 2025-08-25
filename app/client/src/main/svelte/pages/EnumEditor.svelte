<script>
  import { onMount } from "svelte";
  import { createEventDispatcher } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue } from "../utils/rest.js";
  import { updateValue } from "../utils/rest.js";
  import Button from "../components/Button";
  import TextField from "../components/TextField";
  import TextArea from "../components/TextArea";

  export let visible = false;
  export let autofocus = true;
  export let autoscroll = true;
  export let item = undefined;
  export let art;
  export let code;

  let focusOn;
  let bottomDiv;
  let clicked = false;
  let showUpdate;
  let newItem;
  onMount(async () => {
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
    console.log(["onMount", autofocus, autoscroll]);
  });

  $: item ? onChange() : onCreate();
  function onCreate() {
    showUpdate = false;
    newItem = {
      code: code,
      name: "",
      text: "",
    };
    console.log(["onCreate", newItem]);
  }
  function onChange() {
    showUpdate = true;
    newItem = {
      code: code,
      name: item.name,
      text: item.text,
    };
    console.log(["onChange", newItem]);
  }
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

  const dispatch = createEventDispatcher();
  function createItem() {
    createValue("/api/enum/" + art, newItem)
      .then((json) => {
        console.log(["createItem", newItem, json]);
        visible = false;
        dispatch("create", json);
      })
      .catch((err) => {
        console.log(["createItem", newItem, err]);
        toast.push(err.toString());
      });
  }
  function updateItem() {
    updateValue("/api/enum/" + art + "/" + newItem.code, newItem)
      .then((json) => {
        console.log(["updateItem", newItem, json]);
        visible = false;
        dispatch("update", json);
      })
      .catch((err) => {
        console.log(["updateItem", newItem, err]);
        toast.push(err.toString());
      });
  }
</script>

<form on:submit|preventDefault={onSubmit} on:reset|preventDefault={onCancel}>
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
      <slot name="cancel">
        <Button type="reset">Abbrechen</Button>
      </slot>
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
