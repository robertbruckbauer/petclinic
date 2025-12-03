<script>
  import { PetService } from "../../services/pet.service";
  import {
    mapOwnerToOwnerItem,
    OwnerService,
  } from "../../services/owner.service";
  import { EnumService } from "../../services/enum.service";
  import { storedOwner } from "../../stores/owner.js";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast/index.js";
  import Circle from "../../components/Spinner/index.js";
  import Icon from "../../components/Icon/index.js";
  import Select from "../../components/Select/index.js";
  import PetEditor from "./PetEditor.svelte";
  import VisitTreatment from "../clinic/VisitTreatment.svelte";

  const petService = new PetService();
  const ownerService = new OwnerService();
  const enumService = new EnumService();

  let allOwnerItem = $state([]);
  let allSpeciesEnum = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      const search = { sort: "name,asc" };
      ownerService.loadAllOwner(search).subscribe({
        next: (json) => {
          allOwnerItem = json.map(mapOwnerToOwnerItem);
        },
        error: (err) => {
          toast.push(err);
        },
      });
      enumService.loadAllEnum("species").subscribe({
        next: (json) => {
          allSpeciesEnum = json.map((e) => ({
            value: e.value,
            text: e.name,
          }));
        },
        error: (err) => {
          toast.push(err);
        },
      });
      loadAllPet();
    } finally {
      loading = false;
    }
  });

  let petOwnerId = $state($storedOwner.id);
  let petId = $state();
  function onPetClicked(_pet) {
    petId = _pet.id;
  }
  function onPetRemoveClicked(_pet) {
    petId = _pet.id;
    removePet(_pet);
  }

  let petEditorCreate = $state(false);
  function onPetEditorCreateClicked() {
    petEditorCreate = true;
    petEditorUpdate = false;
    visitEditorCreate = false;
  }

  let petEditorUpdate = $state(false);
  function onPetEditorUpdateClicked(_pet) {
    petId = _pet.id;
    petEditorCreate = false;
    petEditorUpdate = true;
    visitEditorCreate = false;
  }

  let visitEditorCreate = $state(false);
  function onTreatmentCreateClicked(_pet) {
    petId = _pet.id;
    petEditorCreate = false;
    petEditorUpdate = false;
    visitEditorCreate = true;
  }

  const petEditorDisabled = $derived(petEditorCreate || petEditorUpdate);

  let allPet = $state([]);
  function onCreatePet(_pet) {
    allPet = allPet.toSpliced(0, 0, _pet);
  }
  function onUpdatePet(_pet) {
    let index = allPet.findIndex((e) => e.id === _pet.id);
    if (index > -1) allPet = allPet.toSpliced(index, 1, _pet);
  }
  function onRemovePet(_pet) {
    let index = allPet.findIndex((e) => e.id === _pet.id);
    if (index > -1) allPet = allPet.toSpliced(index, 1);
  }

  function loadAllPet() {
    const search = { sort: "name,asc", "owner.id": petOwnerId };
    petService.loadAllPet(search).subscribe({
      next: (json) => {
        allPet = json;
        // make that owner persistent
        $storedOwner.id = petOwnerId;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function removePet(_pet) {
    const text = _pet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete pet '" + hint + "' permanently?")) return;
    petService.removePet(_pet.id).subscribe({
      next: (json) => {
        console.log(["removePet", _pet, json]);
        onRemovePet(json);
      },
      error: (err) => {
        console.log(["removePet", _pet, err]);
        toast.push(err);
      },
    });
  }
</script>

<h1>Pet</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <div class="flex-grow">
    <Select
      bind:value={petOwnerId}
      onchange={loadAllPet}
      valueGetter={(v) => v?.value}
      allItem={allOwnerItem}
      disabled={petEditorDisabled}
      label="Owner"
      placeholder="Choose owner"
    />
  </div>
  {#if loading}
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
    </div>
  {:else}
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-200">
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-gray-600">Species</span>
          </th>
          <th class="px-2 py-3 text-left w-2/4 table-cell">
            <span class="text-gray-600">Name</span>
          </th>
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-gray-600">Born</span>
          </th>
          <th class="px-2 py-3 text-right w-0 table-cell">
            <Icon
              onclick={() => onPetEditorCreateClicked()}
              disabled={petEditorDisabled}
              title="add a new Pet"
              name="add"
              outlined
            />
          </th>
        </tr>
      </thead>
      <tbody>
        {#if petEditorCreate}
          <tr>
            <td class="border-l-4 px-2" colspan="4">
              <PetEditor
                bind:visible={petEditorCreate}
                oncreate={onCreatePet}
                {allSpeciesEnum}
                ownerId={petOwnerId}
              />
            </td>
          </tr>
        {/if}
        {#each allPet as pet, i}
          <tr
            onclick={(e) => onPetClicked(pet)}
            title={pet.id}
            class:border-l-2={petId === pet.id}
            class:bg-gray-100={i % 2 === 1}
          >
            <td class="px-2 py-3 text-left table-cell">
              <span>{pet.species}</span>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <div class="text-sm underline text-blue-600">
                <a href={"/pet/" + pet.id}>{pet.name}</a>
              </div>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <span>{pet.born}</span>
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-3 items-center gap-1 w-max"
              >
                <Icon
                  onclick={() => onTreatmentCreateClicked(pet)}
                  disabled={petEditorDisabled}
                  title="Add a treatment"
                  name="event"
                  outlined
                />
                <Icon
                  onclick={() => onPetRemoveClicked(pet)}
                  disabled={petEditorDisabled}
                  title="Delete a pet"
                  name="delete"
                  outlined
                />
                <Icon
                  onclick={() => onPetEditorUpdateClicked(pet)}
                  disabled={petEditorDisabled}
                  title="Edit a visit"
                  name="edit"
                  outlined
                />
              </div>
            </td>
          </tr>
          {#if visitEditorCreate && petId === pet.id}
            <tr>
              <td class="border-l-4 px-2" colspan="4">
                <VisitTreatment bind:visible={visitEditorCreate} {pet} />
              </td>
            </tr>
          {/if}
          {#if petEditorUpdate && petId === pet.id}
            <tr>
              <td class="border-l-4 px-2" colspan="4">
                <PetEditor
                  bind:visible={petEditorUpdate}
                  onupdate={onUpdatePet}
                  {allSpeciesEnum}
                  ownerId={petOwnerId}
                  {pet}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2 py-3" colspan="4">No pets</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
