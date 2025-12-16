<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../controls/Toast";
  import { OwnerService } from "../../services/owner.service";
  import type { Owner } from "../../types/owner.type";

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

  function onSubmitClicked(_event: Event) {
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

  function onCancelClicked(_event: Event) {
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

<form onsubmit={onSubmitClicked}>
  <div class="flex flex-col gap-2 pt-4">
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Name</legend>
      <input
        bind:this={focusOn}
        bind:value={newOwnerName}
        aria-label="Name"
        type="text"
        class="input input-bordered w-full"
        placeholder="Enter a name"
      />
    </fieldset>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Address</legend>
      <input
        bind:value={newOwnerAddress}
        aria-label="Address"
        type="text"
        class="input input-bordered w-full"
        placeholder="Enter an address"
      />
    </fieldset>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Contact</legend>
      <input
        bind:value={newOwnerContact}
        aria-label="Contact"
        type="text"
        class="input input-bordered w-full"
        placeholder="Enter a contact"
      />
    </fieldset>
  </div>
  <div class="join py-4">
    <button type="submit" class="btn join-item">Ok</button>
    <button type="button" class="btn join-item" onclick={onCancelClicked}>
      Cancel
    </button>
  </div>
</form>

<div class="h-0" bind:this={bottomDiv}>&nbsp;</div>

{#if import.meta.env.DEV}
  <details tabindex="-1">
    <summary>JSON</summary>
    <pre>{JSON.stringify(newOwner, null, 2)}</pre>
  </details>
{/if}
