<script>
  import { OwnerService } from "../../services/owner.service";
  import { EnumService } from "../../services/enum.service";
  import { mapVetToVetItem } from "../../services/vet.service";
  import { VisitService } from "../../services/visit.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast/index.js";
  import Circle from "../../components/Spinner/index.js";
  import Icon from "../../components/Icon/index.js";
  import TextField from "../../components/TextField/index.js";
  import OwnerEditor from "./OwnerEditor.svelte";
  import PetEditor from "./PetEditor.svelte";
  import VisitCardLister from "../clinic/VisitCardLister.svelte";

  const ownerService = new OwnerService();
  const enumService = new EnumService();
  const visitService = new VisitService();

  let allVetItem = $state([]);
  let allSpeciesEnum = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      ownerService.loadAllOwner("?sort=name,asc").subscribe({
        next: (json) => {
          allVetItem = json.map(mapVetToVetItem);
        },
        error: (err) => {
          toast.push(err.detail || err.toString());
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
          toast.push(err.detail || err.toString());
        },
      });
      loadAllOwner();
    } finally {
      loading = false;
    }
  });

  let ownerId = $state();
  function onOwnerClicked(_owner) {
    ownerId = _owner.id;
  }
  function onOwnerRemoveClicked(_owner) {
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
  function onOwnerEditorUpdateClicked(_owner) {
    ownerId = _owner.id;
    ownerEditorCreate = false;
    ownerEditorUpdate = true;
    visitLister = false;
    petCreateEditor = false;
  }

  let visitLister = $state(false);
  function onVisitListerClicked(_owner) {
    ownerId = _owner.id;
    ownerEditorCreate = false;
    ownerEditorUpdate = false;
    visitLister = !visitLister;
    petCreateEditor = false;
    if (visitLister) loadAllVisit();
  }

  let petCreateEditor = $state(false);
  function onPetCreateEditorClicked(_owner) {
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

  let ownerFilter = $state("");
  function ownerFilterParameter() {
    if (!ownerFilter) return "";
    return "&name=" + encodeURIComponent(ownerFilter);
  }
  function ownerSortParameter() {
    return "?sort=name";
  }

  function onOwnerFilterClicked(_event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllOwner();
    } finally {
      loading = false;
    }
  }

  let allOwner = $state([]);
  function onCreateOwner(_owner) {
    allOwner = allOwner.toSpliced(0, 0, _owner);
  }
  function onUpdateOwner(_owner) {
    let index = allOwner.findIndex((e) => e.id === _owner.id);
    if (index > -1) allOwner = allOwner.toSpliced(index, 1, _owner);
  }
  function onRemoveOwner(_owner) {
    let index = allOwner.findIndex((e) => e.id === _owner.id);
    if (index > -1) allOwner = allOwner.toSpliced(index, 1);
  }

  function onCreatePet(_owner, _pet) {
    const _petItem = {
      value: _pet.id,
      text: _pet.species + " '" + _pet.name + "'",
    };
    _owner.allPetItem = _owner.allPetItem.toSpliced(0, 0, _petItem);
  }

  function loadAllOwner() {
    const query = ownerSortParameter() + ownerFilterParameter();
    ownerService.loadAllOwner(query).subscribe({
      next: (json) => {
        allOwner = json;
      },
      error: (err) => {
        toast.push(err.detail || err.toString());
      },
    });
  }

  let allOwnerVisit = $state([]);
  function loadAllVisit() {
    const query = "?sort=date,desc&pet.owner.id=" + ownerId;
    visitService.loadAllVisit(query).subscribe({
      next: (json) => {
        allOwnerVisit = json;
      },
      error: (err) => {
        toast.push(err.detail || err.toString());
      },
    });
  }

  function removeOwner(_owner) {
    const text = _owner.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete owner '" + hint + "' permanently?")) return;
    ownerService.removeOwner(_owner.id).subscribe({
      next: (json) => {
        onRemoveOwner(json);
      },
      error: (err) => {
        toast.push(err.detail || err.toString());
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
          <tr>
            <td class="border-l-4 px-2" colspan="3">
              <OwnerEditor
                bind:visible={ownerEditorCreate}
                oncreate={onCreateOwner}
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
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <PetEditor
                  bind:visible={petCreateEditor}
                  oncreate={(pet) => onCreatePet(owner, pet)}
                  {allSpeciesEnum}
                  ownerId={owner.id}
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
