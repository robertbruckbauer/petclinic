<script lang="ts">
  import { onMount } from "svelte";
  import { EnumService } from "../../services/enum.service";
  import type { EnumItem } from "../../types/enum.type";
  import { toast } from "../../components/Toast/index.js";
  import Button from "../../components/Button/index.js";
  import TextField from "../../components/TextField/index.js";
  import TextArea from "../../components/TextArea/index.js";

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

  function handleSubmit(_event: Event) {
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

  function handleCancel(_event: Event) {
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

<form onsubmit={handleSubmit}>
  <div class="flex flex-col gap-1">
    <div class="w-full flex flex-row gap-1 items-baseline">
      <div class="w-1/6">
        <TextField
          bind:value={newItemCode}
          required
          type="number"
          label="Code"
          placeholder="Enter a code"
          disabled={onupdate !== undefined}
        />
      </div>
      <div class="w-full">
        <TextField
          bind:this={focusOn}
          bind:value={newItemName}
          required
          label="Name"
          placeholder="Enter a name"
        />
      </div>
    </div>
    <div class="w-full">
      <TextArea
        bind:value={newItemText}
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
      <Button type="button" onclick={handleCancel}>Abbrechen</Button>
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
