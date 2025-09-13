<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue } from "../utils/rest.js";
  import { updatePatch } from "../utils/rest.js";
  import Button from "../components/Button";
  import TextField from "../components/TextField";

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    owner = {},
    oncancel = undefined,
    oncreate = undefined,
    onupdate = undefined,
  } = $props();

  let clicked = $state(false);
  let focusOn;
  let bottomDiv;
  onMount(async () => {
    console.log(["onMount", autofocus, autoscroll]);
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
  });

  let newOwnerName = $derived(owner.name);
  let newOwnerAddress = $derived(owner.address);
  let newOwnerContact = $derived(owner.contact);
  let newOwner = $derived({
    id: owner.id,
    version: owner.version,
    name: newOwnerName,
    address: newOwnerAddress,
    contact: newOwnerContact,
  });

  function handleSubmit(_event) {
    _event.preventDefault();
    try {
      clicked = true;
      if (owner.id) {
        updateOwner();
      } else {
        createOwner();
      }
    } finally {
      clicked = false;
    }
  }

  function handleCancel(_event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function createOwner() {
    createValue("/api/owner", newOwner)
      .then((json) => {
        console.log(["createOwner", newOwner, json]);
        visible = false;
        oncreate?.(json);
      })
      .catch((err) => {
        console.log(["createOwner", newOwner, err]);
        toast.push(err.toString());
      });
  }

  function updateOwner() {
    updatePatch("/api/owner" + "/" + newOwner.id, newOwner)
      .then((json) => {
        console.log(["updateOwner", newOwner, json]);
        visible = false;
        onupdate?.(json);
      })
      .catch((err) => {
        console.log(["updateOwner", newOwner, err]);
        toast.push(err.toString());
      });
  }
</script>

<form onsubmit={handleSubmit}>
  <div class="flex flex-col gap-1">
    <div class="w-full">
      <TextField
        bind:this={focusOn}
        bind:value={newOwnerName}
        required
        label="Name"
        placeholder="Insert a name"
      />
    </div>
    <div class="w-full">
      <TextField
        bind:value={newOwnerAddress}
        required
        label="Address"
        placeholder="Insert a text"
      />
    </div>
    <div class="w-full">
      <TextField
        bind:value={newOwnerContact}
        required
        label="Contact"
        placeholder="Insert a text"
      />
    </div>
  </div>
  <div class="py-4 flex flex-row gap-1 items-baseline">
    <div class="flex-initial">
      <Button type="submit">Ok</Button>
    </div>
    <div class="flex-initial">
      <Button type="button" onclick={handleCancel}>Abbrechen</Button>
    </div>
  </div>
</form>

<div class="h-0" bind:this={bottomDiv}>&nbsp;</div>

{#if import.meta.env.DEV}
  <details tabindex="-1">
    <summary>JSON</summary>
    <pre>{JSON.stringify(newOwner, null, 2)}</pre>
  </details>
{/if}
