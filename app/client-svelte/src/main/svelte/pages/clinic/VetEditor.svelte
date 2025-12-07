<script lang="ts">
  import { onMount } from "svelte";
  import { VetService } from "../../services/vet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Vet } from "../../types/vet.type";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import TextField from "../../components/TextField";
  import Toggle from "../../components/Toggle";

  const vetService = new VetService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    allSkillEnum: EnumItem[];
    vet: Vet;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((vet: Vet) => void);
    onupdate?: undefined | ((vet: Vet) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    allSkillEnum,
    vet = {} as Vet,
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

  let newVetName = $state("");
  let newVetAllSkill = $state([] as string[]);
  $effect(() => {
    newVetName = vet.name;
    newVetAllSkill = vet.allSkill || [];
  });
  const newVet = $derived({
    ...vet,
    name: newVetName,
    allSkill: newVetAllSkill,
  });

  function handleSubmit(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      if (vet.id) {
        updateVet();
      } else {
        createVet();
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

  function createVet() {
    vetService.createVet(newVet).subscribe({
      next: (json) => {
        visible = false;
        oncreate?.(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function updateVet() {
    vetService.updateVet(newVet).subscribe({
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
    <TextField
      bind:this={focusOn}
      bind:value={newVetName}
      required
      label="Name"
      placeholder="Bitte einen Namen eingeben"
    />
    <Toggle
      bind:allValue={newVetAllSkill}
      allItem={allSkillEnum}
      label="Skills"
      placeholder="Insert skills"
    />
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
    <pre>{JSON.stringify(newVet, null, 2)}</pre>
  </details>
{/if}
