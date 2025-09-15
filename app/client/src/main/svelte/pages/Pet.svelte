<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import { storedOwner } from "../stores/owner.js";
  import Circle from "../components/Spinner";
  import Icon from "../components/Icon";
  import Select from "../components/Select";
  import PetEditor from "./PetEditor.svelte";
  import VisitTreatment from "./VisitTreatment.svelte";

  let allOwnerItem = $state([]);
  let allSpeciesEnum = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      allOwnerItem = await loadAllValue("/api/owner?sort=name,asc");
      allOwnerItem = allOwnerItem.map((e) => ({
        value: e.id,
        text: e.name + ", " + e.address,
      }));
      console.log(["onMount", allOwnerItem]);
      allSpeciesEnum = await loadAllValue("/api/enum/species");
      allSpeciesEnum = allSpeciesEnum.map((e) => ({
        value: e.value,
        text: e.name,
      }));
      console.log(["onMount", allSpeciesEnum]);
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    } finally {
      loading = false;
    }
  });

  let petOwnerId = $derived($storedOwner.id);
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
  }

  let petEditorUpdate = $state(false);
  function onPetEditorUpdateClicked(_pet) {
    petId = _pet.id;
    petEditorUpdate = true;
  }

  let visitEditorCreate = $state(false);
  function onVisitEditorCreateClicked(_pet) {
    petId = _pet.id;
    visitEditorCreate = true;
  }

  let petEditorDisabled = $derived(petEditorCreate || petEditorUpdate);

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
    const query = "?owner.id=" + petOwnerId;
    loadAllValue("/api/pet" + query)
      .then((json) => {
        const msg = import.meta.env.DEV ? json : json.length;
        console.log(["loadAllPet", query, msg]);
        allPet = json;
      })
      .catch((err) => {
        console.log(["loadAllPet", query, err]);
        toast.push(err.toString());
      });
  }

  function removePet(_pet) {
    const text = _pet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Pet '" + hint + "' wirklich löschen?")) return;
    removeValue("/api/pet/" + _pet.id)
      .then((json) => {
        console.log(["removePet", _pet, json]);
        onRemovePet(json);
      })
      .catch((err) => {
        console.log(["removePet", _pet, err]);
        toast.push(err.toString());
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
        <tr class="bg-title-100">
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-gray-600">Species</span>
          </th>
          <th class="px-2 py-3 text-left w-2/4 table-cell">
            <span class="text-title-600">Name</span>
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
                  onclick={() => onVisitEditorCreateClicked(pet)}
                  disabled={petEditorDisabled}
                  title="Add a new visit"
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
                <VisitTreatment
                  bind:visible={visitEditorCreate}
                  oncreate={loadAllPet}
                  {pet}
                />
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
