<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../controls/Toast";
  import { EnumService } from "../../services/enum.service";
  import type { EnumItem } from "../../types/enum.type";

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    art: "species" | "skill";
    item: EnumItem;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((item: EnumItem) => void);
    onupdate?: undefined | ((item: EnumItem) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    art,
    item = {} as EnumItem,
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

  let newItemCode = $state(0);
  let newItemName = $state("");
  let newItemText = $state("");
  $effect(() => {
    newItemCode = item.code;
    newItemName = item.name;
    newItemText = item.text;
  });
  const newItem: EnumItem = $derived({
    ...item,
    code: newItemCode,
    name: newItemName,
    text: newItemText,
  });

  function onSubmitClicked(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      if (oncreate !== undefined) {
        createItem();
      }
      if (onupdate !== undefined) {
        updateItem();
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

  const enumService = new EnumService();

  function createItem() {
    enumService.createEnum(art, newItem).subscribe({
      next: (json) => {
        visible = false;
        oncreate?.(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function updateItem() {
    enumService.updateEnum(art, newItem).subscribe({
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
    <div class="w-full flex flex-row gap-1 items-baseline">
      <fieldset class="fieldset w-24">
        <legend class="fieldset-legend">Code</legend>
        <input
          bind:value={newItemCode}
          aria-label="Code"
          type="number"
          class="input w-full text-center"
          readonly={onupdate !== undefined}
          placeholder="Enter a code"
        />
      </fieldset>
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Name</legend>
        <input
          bind:this={focusOn}
          bind:value={newItemName}
          aria-label="Name"
          type="text"
          class="input w-full"
          placeholder="Enter a name"
        />
      </fieldset>
    </div>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Text</legend>
      <textarea
        bind:value={newItemText}
        aria-label="Text"
        class="textarea w-full"
        placeholder="Enter a text"
      ></textarea>
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
    <pre>{JSON.stringify(newItem, null, 2)}</pre>
  </details>
{/if}
