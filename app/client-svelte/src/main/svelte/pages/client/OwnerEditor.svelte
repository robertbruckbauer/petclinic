<script lang="ts">
  import { onMount } from "svelte";
  import { OwnerService } from "../../services/owner.service";
  import type { Owner } from "../../types/owner.type";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import TextField from "../../components/TextField";

  const ownerService = new OwnerService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    owner: Owner;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((owner: Owner) => void);
    onupdate?: undefined | ((owner: Owner) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    owner = $bindable({} as Owner),
    oncancel = undefined,
    oncreate = undefined,
    onupdate = undefined,
  }: Props = $props();

  let clicked = $state(false);
  let focusOn: any;
  let bottomDiv: HTMLElement;
  onMount(async () => {
    console.log(["onMount", autofocus, autoscroll]);
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
  });

  let newOwnerName = $state("");
  let newOwnerAddress = $state("");
  let newOwnerContact = $state("");
  $effect(() => {
    newOwnerName = owner.name;
    newOwnerAddress = owner.address;
    newOwnerContact = owner.contact;
  });
  const newOwner: Owner = $derived({
    ...owner,
    name: newOwnerName,
    address: newOwnerAddress,
    contact: newOwnerContact,
  });

  function handleSubmit(_event: Event) {
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

  function handleCancel(_event: Event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function createOwner() {
    ownerService.createOwner(newOwner).subscribe({
      next: (json) => {
        visible = false;
        oncreate?.(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function updateOwner() {
    ownerService.updateOwner(newOwner).subscribe({
      next: (json) => {
        visible = false;
        onupdate?.(json);
      },
      error: (err) => {
        toast.push(err);
      },
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
