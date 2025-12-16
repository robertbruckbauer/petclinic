<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../controls/Toast";
  import { PetService } from "../../services/pet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { OwnerItem } from "../../types/owner.type";
  import type { Pet } from "../../types/pet.type";

  const petService = new PetService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    allSpeciesEnum: EnumItem[];
    pet: Pet;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((pet: Pet) => void);
    onupdate?: undefined | ((pet: Pet) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    allSpeciesEnum,
    pet = {} as Pet,
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

  let newPetOwnerItem = $state({} as OwnerItem);
  let newPetSpecies = $state("");
  let newPetName = $state("");
  let newPetBorn = $state("");
  $effect(() => {
    newPetOwnerItem = pet.ownerItem!;
    newPetSpecies = pet.species;
    newPetName = pet.name;
    newPetBorn = pet.born;
  });
  const newPet: Pet = $derived({
    ...pet,
    owner: "/api/owner/" + newPetOwnerItem.value,
    species: newPetSpecies,
    name: newPetName,
    born: newPetBorn,
  });

  function onSubmitClicked(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      if (pet.id) {
        updatePet();
      } else {
        createPet();
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

  function createPet() {
    petService.createPet(newPet).subscribe({
      next: (json) => {
        visible = false;
        oncreate?.(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function updatePet() {
    petService.updatePet(newPet).subscribe({
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
  <div class="flex flex-col sm:flex-row gap-2 pt-4">
    <fieldset class="fieldset w-full sm:w-1/4">
      <legend class="fieldset-legend">Species</legend>
      <select
        bind:this={focusOn}
        bind:value={newPetSpecies}
        aria-label="Species"
        class="select w-full"
      >
        <option value="" disabled selected>Choose a species</option>
        {#each allSpeciesEnum as speciesEnum}
          <option value={speciesEnum.name} title={speciesEnum.text}>
            {speciesEnum.name}
          </option>
        {/each}
      </select>
    </fieldset>
    <fieldset class="fieldset w-full sm:w-2/4">
      <legend class="fieldset-legend">Name</legend>
      <input
        bind:value={newPetName}
        aria-label="Name"
        type="text"
        class="input input-bordered w-full"
        placeholder="Enter a name"
      />
    </fieldset>
    <fieldset class="fieldset w-full sm:w-1/4">
      <legend class="fieldset-legend">Born</legend>
      <input
        bind:value={newPetBorn}
        aria-label="Born"
        type="date"
        class="input input-bordered w-full"
        placeholder="Enter a date"
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
    <pre>{JSON.stringify(newPet, null, 2)}</pre>
  </details>
{/if}
