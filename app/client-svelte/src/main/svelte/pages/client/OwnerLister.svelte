<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { EnumService } from "../../services/enum.service";
  import { OwnerService } from "../../services/owner.service";
  import { VisitService } from "../../services/visit.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Owner } from "../../types/owner.type";
  import type { Pet, PetItem } from "../../types/pet.type";
  import type { Visit } from "../../types/visit.type";
  import { toast } from "../../components/Toast/index.js";
  import Circle from "../../components/Spinner/index.js";
  import Icon from "../../components/Icon/index.js";
  import TextField from "../../components/TextField/index.js";
  import OwnerEditor from "./OwnerEditor.svelte";
  import PetEditor from "./PetEditor.svelte";
  import VisitCardLister from "../clinic/VisitCardLister.svelte";

  const enumService = new EnumService();
  const ownerService = new OwnerService();
  const visitService = new VisitService();

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

  let ownerId: string | undefined = $state();
  function onOwnerClicked(_owner: Owner) {
    ownerId = _owner.id;
  }
  function onOwnerRemoveClicked(_owner: Owner) {
    ownerId = _owner.id;
    removeOwner(_owner);
  }

  let ownerEditorCreate = $state(false);
  function onOwnerEditorCreateClicked() {
    ownerEditorCreate = true;
    ownerEditorUpdate = false;
    visitLister = false;
    petCreateEditor = false;
  }

  let ownerEditorUpdate = $state(false);
  function onOwnerEditorUpdateClicked(_owner: Owner) {
    ownerId = _owner.id;
    ownerEditorCreate = false;
    ownerEditorUpdate = true;
    visitLister = false;
    petCreateEditor = false;
  }

  let visitLister = $state(false);
  function onVisitListerClicked(_owner: Owner) {
    ownerId = _owner.id;
    ownerEditorCreate = false;
    ownerEditorUpdate = false;
    visitLister = !visitLister;
    petCreateEditor = false;
    if (visitLister) loadAllVisit();
  }

  let petCreateEditor = $state(false);
  function onPetCreateEditorClicked(_owner: Owner) {
    ownerId = _owner.id;
    ownerEditorCreate = false;
    ownerEditorUpdate = false;
    visitLister = false;
    petCreateEditor = true;
    visitLister = false;
  }

  const ownerEditorDisabled = $derived(
    ownerEditorCreate || ownerEditorUpdate || petCreateEditor
  );

  function onOwnerFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllOwner();
    } finally {
      loading = false;
    }
  }

  let allOwner: Owner[] = $state([]);
  function onCreateOwner(_owner: Owner) {
    allOwner = [_owner, ...allOwner];
  }
  function onUpdateOwner(_owner: Owner) {
    allOwner = allOwner.map((e) => (e.id === _owner.id ? _owner : e));
  }
  function onRemoveOwner(_owner: Owner) {
    allOwner = allOwner.filter((e) => e.id !== _owner.id);
  }

  function onCreatePet(_owner: Owner, _pet: Pet) {
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

  let allOwnerVisit: Visit[] = $state([]);
  function loadAllVisit() {
    if (ownerId) {
      const search = { sort: "date,desc", "pet.owner.id": ownerId };
      visitService.loadAllVisit(search).subscribe({
        next: (json) => {
          allOwnerVisit = json;
        },
        error: (err) => {
          toast.push(err);
        },
      });
    } else {
      allOwnerVisit = [];
    }
  }

  function removeOwner(_owner: Owner) {
    const text = _owner.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete owner '" + hint + "' permanently?")) return;
    ownerService.removeOwner(_owner.id!).subscribe({
      next: (json) => {
        onRemoveOwner(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }
</script>

<h1>Owner</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onOwnerFilterClicked}>
    <div class="flex flex-row gap-1 items-center pr-2">
      <div class="w-full">
        <TextField
          bind:value={ownerFilter}
          label="Filter"
          placeholder="Bitte Filterkriterien eingeben"
        />
      </div>
      <div class="w-min">
        <Icon type="submit" name="search" outlined />
      </div>
    </div>
  </form>
  {#if loading}
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
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
            <Icon
              onclick={() => onOwnerEditorCreateClicked()}
              disabled={ownerEditorDisabled}
              title="Add a new owner"
              name="add"
              outlined
            />
          </th>
        </tr>
      </thead>
      <tbody>
        {#if ownerEditorCreate}
          {@const owner: Owner = {
              version: 0,
              name: "",
              address: "",
              contact: "",
            }}
          <tr>
            <td class="border-l-4 px-2" colspan="3">
              <OwnerEditor
                bind:visible={ownerEditorCreate}
                oncreate={onCreateOwner}
                {owner}
              />
            </td>
          </tr>
        {/if}
        {#each allOwner as owner, i}
          <tr
            onclick={(e) => onOwnerClicked(owner)}
            title={owner.id}
            class:border-l-2={ownerId === owner.id}
            class:bg-gray-100={i % 2 === 1}
          >
            <td class="px-2 py-3 text-left table-cell">
              <div class="text-sm underline text-blue-600">
                <a href={"/owner/" + owner.id}>{owner.name}</a>
              </div>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <div class="flex flex-col">
                {#each owner.allPetItem as petItem}
                  <div class="text-sm underline text-blue-600">
                    <a href={"/pet/" + petItem.value}>{petItem.text}</a>
                  </div>
                {:else}
                  <span>No pets</span>
                {/each}
              </div>
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-4 items-center gap-1 w-max"
              >
                <Icon
                  onclick={() => onVisitListerClicked(owner)}
                  disabled={ownerEditorDisabled}
                  title="Show all visits"
                  name="list"
                  outlined
                />
                <Icon
                  onclick={() => onPetCreateEditorClicked(owner)}
                  disabled={ownerEditorDisabled}
                  title="Add a new pet"
                  name="pets"
                  outlined
                />
                <Icon
                  onclick={() => onOwnerRemoveClicked(owner)}
                  disabled={ownerEditorDisabled}
                  title="Delete an owner"
                  name="delete"
                  outlined
                />
                <Icon
                  onclick={() => onOwnerEditorUpdateClicked(owner)}
                  disabled={ownerEditorDisabled}
                  title="Edit an owner"
                  name="edit"
                  outlined
                />
              </div>
            </td>
          </tr>
          {#if visitLister && ownerId === owner.id}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <VisitCardLister allVisit={allOwnerVisit} />
              </td>
            </tr>
          {/if}
          {#if petCreateEditor && ownerId === owner.id}
            {@const pet: Pet = {
              version: 0,
              ownerItem: { value: ownerId!, text: "" },
              name: "",
              born: "",
              species: "",
            }}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <PetEditor
                  bind:visible={petCreateEditor}
                  oncreate={(pet) => onCreatePet(owner, pet)}
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
                  bind:visible={ownerEditorUpdate}
                  onupdate={onUpdateOwner}
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
