<script>
  import * as restApi from "../services/rest.js";
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import Button from "../components/Button";
  import Select from "../components/Select";
  import TextField from "../components/TextField";

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    allSpeciesEnum,
    ownerId,
    pet = {},
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

  let newPetSpecies = $state();
  let newPetName = $state();
  let newPetBorn = $state();
  $effect(() => {
    newPetSpecies = pet.species;
    newPetName = pet.name;
    newPetBorn = pet.born;
  });
  const newPet = $derived({
    id: pet.id,
    version: pet.version,
    owner: "/api/owner/" + ownerId,
    species: newPetSpecies,
    name: newPetName,
    born: newPetBorn,
  });

  function handleSubmit(_event) {
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

  function handleCancel(_event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function createPet() {
    restApi
      .createValue("/api/pet", newPet)
      .then((json) => {
        console.log(["createPet", newPet, json]);
        visible = false;
        oncreate?.(json);
      })
      .catch((err) => {
        console.log(["createPet", newPet, err]);
        toast.push(err.toString());
      });
  }

  function updatePet() {
    restApi
      .updatePatch("/api/pet" + "/" + newPet.id, newPet)
      .then((json) => {
        console.log(["updatePet", newPet, json]);
        visible = false;
        onupdate?.(json);
      })
      .catch((err) => {
        console.log(["updatePet", newPet, err]);
        toast.push(err.toString());
      });
  }
</script>

<form onsubmit={handleSubmit}>
  <div class="flex flex-col gap-1">
    <div class="w-full lg:w-1/4">
      <Select
        bind:this={focusOn}
        bind:value={newPetSpecies}
        valueGetter={(v) => v?.value}
        allItem={allSpeciesEnum}
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
