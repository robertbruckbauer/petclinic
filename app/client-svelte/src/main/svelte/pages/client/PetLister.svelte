<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../controls/Toast";
  import { storedOwner } from "../../stores/owner.store";
  import { EnumService } from "../../services/enum.service";
  import { OwnerService } from "../../services/owner.service";
  import { mapPetToPetItem, PetService } from "../../services/pet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { OwnerItem } from "../../types/owner.type";
  import type { Pet } from "../../types/pet.type";
  import type { Visit } from "../../types/visit.type";
  import PetEditor from "./PetEditor.svelte";
  import VisitTreatment from "../clinic/VisitTreatment.svelte";

  const enumService = new EnumService();
  const ownerService = new OwnerService();
  const petService = new PetService();

  let allOwnerItem: OwnerItem[] = $state([]);
  let allSpeciesEnum: EnumItem[] = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        allSpeciesEnum: enumService.loadAllEnum("species"),
        allOwnerItem: ownerService.loadAllOwnerItem(),
      }).subscribe({
        next: (value) => {
          allSpeciesEnum = value.allSpeciesEnum;
          allOwnerItem = value.allOwnerItem;
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

  let petOwnerId = $state($storedOwner.id || "");
  let petId = $state("");
  function onPetClicked(_pet: Pet) {
    petId = _pet.id!;
  }
  function onPetRemoveClicked(_pet: Pet) {
    petId = _pet.id!;
    removePet(_pet);
  }

  let petEditorCreate = $state(false);
  function onPetEditorCreateClicked() {
    petId = "";
    petEditorCreate = true;
    petEditorUpdate = false;
    visitTreatementCreate = false;
  }

  let petEditorUpdate = $state(false);
  function onPetEditorUpdateClicked(_pet: Pet) {
    petId = _pet.id!;
    petEditorCreate = false;
    petEditorUpdate = true;
    visitTreatementCreate = false;
  }

  let visitTreatementCreate = $state(false);
  function onTreatmentCreateClicked(_pet: Pet) {
    petId = _pet.id!;
    petEditorCreate = false;
    petEditorUpdate = false;
    visitTreatementCreate = true;
  }

  const petFilterDisabled = $derived(petEditorCreate || petEditorUpdate);

  const petEditorDisabled = $derived(petFilterDisabled || petOwnerId === "");

  function onFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllPet();
    } finally {
      loading = false;
    }
  }

  let allPet: Pet[] = $state([]);
  // tag::afterCreate[]
  function afterCreatePet(_pet: Pet) {
    allPet = [_pet, ...allPet];
  }
  // end::afterCreate[]
  // tag::afterUpdate[]
  function afterUpdatePet(_pet: Pet) {
    allPet = allPet.map((e) => (e.id === _pet.id ? _pet : e));
  }
  // end::afterUpdate[]
  // tag::afterRemove[]
  function afterRemovePet(_pet: Pet) {
    allPet = allPet.filter((e) => e.id !== _pet.id);
  }
  // end::afterRemove[]

  function loadAllPet() {
    if (petOwnerId) {
      // tag::loadAll[]
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
      // end::loadAll[]
    } else {
      allPet = [];
    }
  }

  // tag::remove[]
  function removePet(_pet: Pet) {
    const text = _pet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete pet '" + hint + "' permanently?")) return;
    petService.removePet(_pet.id!).subscribe({
      next: (json) => {
        console.log(["removePet", _pet, json]);
        afterRemovePet(json);
      },
      error: (err) => {
        console.log(["removePet", _pet, err]);
        toast.push(err);
      },
    });
  }
  // end::remove[]
</script>

<h1>Pet</h1>

<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onFilterClicked}>
    <div class="flex flex-row gap-2 items-center pb-2 pr-2">
      <select
        bind:value={petOwnerId}
        aria-label="Filter"
        class="select w-full"
        class:pointer-events-none={petFilterDisabled}
        onchange={onFilterClicked}
      >
        <option value="" disabled selected>Choose an owner</option>
        {#each allOwnerItem as ownerItem}
          <option value={ownerItem.value}>{ownerItem.text}</option>
        {/each}
      </select>
    </div>
  </form>
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {:else}
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-200">
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-gray-600">Species</span>
          </th>
          <th class="px-2 py-3 text-left w-full table-cell">
            <span class="text-gray-600">Name</span>
          </th>
          <th class="px-2 py-3 text-right w-0 table-cell">
            <button
              title="Add a new pet"
              class="btn btn-circle btn-outline"
              onclick={onPetEditorCreateClicked}
              disabled={petEditorDisabled}
            >
              <span class="material-icons">add</span>
            </button>
          </th>
        </tr>
      </thead>
      <tbody>
        {#if petEditorCreate}
          {@const pet: Pet = {
            version: 0,
            ownerItem: { value: petOwnerId!, text: "" },
            name: "",
            born: "",
            species: "",
          }}
          <tr>
            <td class="border-l-4 px-2" colspan="3">
              <PetEditor
                oncreate={afterCreatePet}
                bind:visible={petEditorCreate}
                {allSpeciesEnum}
                {pet}
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
              <a href={"/pet/" + pet.id}>{pet.name}</a>
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-3 items-center gap-1 w-max"
              >
                <button
                  title="Add a treatment"
                  class="btn btn-circle btn-outline"
                  onclick={() => onTreatmentCreateClicked(pet)}
                  disabled={petEditorDisabled}
                >
                  <span class="material-icons">event</span>
                </button>
                <button
                  title="Delete a pet"
                  class="btn btn-circle btn-outline"
                  onclick={() => onPetRemoveClicked(pet)}
                  disabled={petEditorDisabled}
                >
                  <span class="material-icons">delete</span>
                </button>
                <button
                  title="Edit a pet"
                  class="btn btn-circle btn-outline"
                  onclick={() => onPetEditorUpdateClicked(pet)}
                  disabled={petEditorDisabled}
                >
                  <span class="material-icons">edit</span>
                </button>
              </div>
            </td>
          </tr>
          {#if visitTreatementCreate && petId === pet.id}
            {@const visit: Visit = {version: 0, petItem: mapPetToPetItem(pet), date: ""}}
            <tr>
              <td class="border-l-4 px-2" colspan="4">
                <VisitTreatment bind:visible={visitTreatementCreate} {visit} />
              </td>
            </tr>
          {/if}
          {#if petEditorUpdate && petId === pet.id}
            <tr>
              <td class="border-l-4 px-2" colspan="4">
                <PetEditor
                  bind:visible={petEditorUpdate}
                  onupdate={afterUpdatePet}
                  {allSpeciesEnum}
                  {pet}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2 py-3" colspan="3">No pets</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
