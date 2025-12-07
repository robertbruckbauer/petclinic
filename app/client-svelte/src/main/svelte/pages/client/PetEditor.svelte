<script lang="ts">
  import { onMount } from "svelte";
  import { PetService } from "../../services/pet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { OwnerItem } from "../../types/owner.type";
  import type { Pet } from "../../types/pet.type";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import Select from "../../components/Select";
  import TextField from "../../components/TextField";

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

  function handleSubmit(_event: Event) {
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

  function handleCancel(_event: Event) {
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

<form onsubmit={handleSubmit}>
  <div class="flex flex-col gap-1">
    <div class="w-full lg:w-1/4">
      <Select
        bind:this={focusOn}
        bind:value={newPetSpecies}
        allItem={allSpeciesEnum.map((e) => e.name)}
        label="Species"
        placeholder="Choose species"
      />
    </div>
    <div class="w-full lg:w-2/4">
      <TextField
        bind:value={newPetName}
        label="Name"
        placeholder="Insert a name"
      />
    </div>
    <div class="w-full lg:w-1/4">
      <TextField
        bind:value={newPetBorn}
        type="date"
        label="Born"
        placeholder="Insert a date"
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
    <pre>{JSON.stringify(newPet, null, 2)}</pre>
  </details>
{/if}
