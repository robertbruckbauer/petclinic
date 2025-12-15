<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast/index.js";
  import { EnumService } from "../../services/enum.service";
  import {
    mapOwnerToOwnerItem,
    OwnerService,
  } from "../../services/owner.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Owner } from "../../types/owner.type";
  import type { Pet, PetItem } from "../../types/pet.type";
  import OwnerEditor from "./OwnerEditor.svelte";
  import PetEditor from "./PetEditor.svelte";
  import VisitOverview from "../clinic/VisitOverview.svelte";

  const enumService = new EnumService();
  const ownerService = new OwnerService();

  let allSpeciesEnum: EnumItem[] = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        allSpeciesEnum: enumService.loadAllEnum("species"),
      }).subscribe({
        next: (value) => {
          allSpeciesEnum = value.allSpeciesEnum;
        },
        error: (err) => {
          toast.push(err);
        },
      });
      loadAllOwner();
    } finally {
      loading = false;
    }
  });

  let ownerId = $state("");
  function onOwnerClicked(_owner: Owner) {
    ownerId = _owner.id!;
  }
  function onOwnerRemoveClicked(_owner: Owner) {
    ownerId = _owner.id!;
    removeOwner(_owner);
  }

  let ownerEditorCreate = $state(false);
  function onOwnerEditorCreateClicked() {
    ownerId = "";
    ownerEditorCreate = true;
    ownerEditorUpdate = false;
    visitLister = false;
    petEditorCreate = false;
  }

  let ownerEditorUpdate = $state(false);
  function onOwnerEditorUpdateClicked(_owner: Owner) {
    ownerId = _owner.id!;
    ownerEditorCreate = false;
    ownerEditorUpdate = true;
    visitLister = false;
    petEditorCreate = false;
  }

  let visitLister = $state(false);
  function onVisitListerClicked(_owner: Owner) {
    ownerId = _owner.id!;
    ownerEditorCreate = false;
    ownerEditorUpdate = false;
    visitLister = !visitLister;
    petEditorCreate = false;
  }

  let petEditorCreate = $state(false);
  function onPetEditorCreateClicked(_owner: Owner) {
    ownerId = _owner.id!;
    ownerEditorCreate = false;
    ownerEditorUpdate = false;
    visitLister = false;
    petEditorCreate = true;
    visitLister = false;
  }

  const ownerFilterDisabled = $derived(
    ownerEditorCreate || ownerEditorUpdate || petEditorCreate
  );

  const ownerEditorDisabled = $derived(ownerFilterDisabled);

  function onFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllOwner();
    } finally {
      loading = false;
    }
  }

  let allOwner: Owner[] = $state([]);
  function afterCreateOwner(_owner: Owner) {
    allOwner = [_owner, ...allOwner];
  }
  function afterUpdateOwner(_owner: Owner) {
    allOwner = allOwner.map((e) => (e.id === _owner.id ? _owner : e));
  }
  function afterRemoveOwner(_owner: Owner) {
    allOwner = allOwner.filter((e) => e.id !== _owner.id);
  }

  function afterCreatePet(_owner: Owner, _pet: Pet) {
    const _petItem: PetItem = {
      value: _pet.id!,
      text: _pet.species + " '" + _pet.name + "'",
    };
    if (_owner.allPetItem) {
      _owner.allPetItem = [_petItem, ..._owner.allPetItem!];
    } else {
      _owner.allPetItem = [_petItem];
    }
  }

  let ownerFilter = $state("");
  function loadAllOwner() {
    const search = { sort: "name,asc", name: ownerFilter ? ownerFilter : "%" };
    ownerService.loadAllOwner(search).subscribe({
      next: (json) => {
        allOwner = json;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function removeOwner(_owner: Owner) {
    const text = _owner.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete owner '" + hint + "' permanently?")) return;
    ownerService.removeOwner(_owner.id!).subscribe({
      next: (json) => {
        afterRemoveOwner(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }
</script>

<h1>Owner</h1>

<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onFilterClicked}>
    <div class="flex flex-row gap-2 items-center pb-2 pr-2">
      <input
        bind:value={ownerFilter}
        aria-label="Filter"
        type="text"
        class="input input-bordered w-full"
        readonly={ownerFilterDisabled}
        placeholder="Enter filter critria"
      />
      <button
        type="submit"
        title="Filter items"
        class="btn btn-circle btn-outline"
        disabled={ownerFilterDisabled}
      >
        <span class="material-icons">search</span>
      </button>
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
          <th class="px-2 py-3 text-left w-1/3 table-cell">
            <span class="text-gray-600">Name</span>
          </th>
          <th class="px-2 py-3 text-left w-full table-cell">
            <span class="text-gray-600">Pets</span>
          </th>
          <th class="px-2 py-3 text-right w-0 table-cell">
            <button
              title="Add a new owner"
              class="btn btn-circle btn-outline"
              onclick={onOwnerEditorCreateClicked}
              disabled={ownerEditorDisabled}
            >
              <span class="material-icons">add</span>
            </button>
          </th>
        </tr>
      </thead>
      <tbody>
        {#if ownerEditorCreate}
          {@const owner: Owner = {version: 0, name: "", address: "", contact: ""}}
          <tr>
            <td class="border-l-4 px-2" colspan="3">
              <OwnerEditor
                oncreate={afterCreateOwner}
                bind:visible={ownerEditorCreate}
                {owner}
              />
            </td>
          </tr>
        {/if}
        {#each allOwner as owner, i}
          <tr
            onclick={() => onOwnerClicked(owner)}
            title={owner.name}
            class:border-l-2={ownerId === owner.id}
            class:bg-gray-100={i % 2 === 1}
          >
            <td class="px-2 py-3 text-left table-cell">
              <span>{owner.name}</span>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <div class="flex flex-col text-sm">
                {#each owner.allPetItem as petItem}
                  <span>{petItem.text}</span>
                {:else}
                  <span>No pets</span>
                {/each}
              </div>
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-4 items-center gap-1 w-max"
              >
                <button
                  title="Show all visits"
                  class="btn btn-circle btn-outline"
                  onclick={() => onVisitListerClicked(owner)}
                >
                  <span class="material-icons">list</span>
                </button>
                <button
                  title="Add a new pet"
                  class="btn btn-circle btn-outline"
                  onclick={() => onPetEditorCreateClicked(owner)}
                  disabled={ownerEditorDisabled}
                >
                  <span class="material-icons">pets</span>
                </button>
                <button
                  title="Delete an owner"
                  class="btn btn-circle btn-outline"
                  onclick={() => onOwnerRemoveClicked(owner)}
                  disabled={ownerEditorDisabled}
                >
                  <span class="material-icons">delete</span>
                </button>
                <button
                  title="Edit an owner"
                  class="btn btn-circle btn-outline"
                  onclick={() => onOwnerEditorUpdateClicked(owner)}
                  disabled={ownerEditorDisabled}
                >
                  <span class="material-icons">edit</span>
                </button>
              </div>
            </td>
          </tr>
          {#if visitLister && ownerId === owner.id}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <VisitOverview {owner} />
              </td>
            </tr>
          {/if}
          {#if petEditorCreate && ownerId === owner.id}
            {@const pet: Pet = {version: 0, ownerItem: mapOwnerToOwnerItem(owner), name: "", born: "", species: "" }}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <PetEditor
                  oncreate={(pet) => afterCreatePet(owner, pet)}
                  bind:visible={petEditorCreate}
                  {allSpeciesEnum}
                  {pet}
                />
              </td>
            </tr>
          {/if}
          {#if ownerEditorUpdate && ownerId === owner.id}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <OwnerEditor
                  onupdate={(owner) => afterUpdateOwner(owner)}
                  bind:visible={ownerEditorUpdate}
                  {owner}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="3">No owners</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
