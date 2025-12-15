<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";
  import { VetService } from "../../services/vet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Vet } from "../../types/vet.type";

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

  function onSubmitClicked(_event: Event) {
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

  function onCancelClicked(_event: Event) {
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

<form onsubmit={onSubmitClicked}>
  <div class="flex flex-col gap-2 pt-4">
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Name</legend>
      <input
        bind:this={focusOn}
        bind:value={newVetName}
        aria-label="Name"
        type="text"
        class="input input-bordered w-full"
        placeholder="Enter a name"
      />
    </fieldset>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Skills</legend>
      <select
        bind:value={newVetAllSkill}
        aria-label="Skills"
        multiple
        size={allSkillEnum.length}
        class="select w-full"
        placeholder="Choose skills"
      >
        {#each allSkillEnum as skillEnum}
          <option value={skillEnum.name} title={skillEnum.text}
            >{skillEnum.name}</option
          >
        {/each}
      </select>
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
    <pre>{JSON.stringify(newVet, null, 2)}</pre>
  </details>
{/if}
