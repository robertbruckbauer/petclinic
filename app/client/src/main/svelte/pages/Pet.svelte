<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { storedOwner } from "../stores/owner.js";
  import Icon from "../components/Icon";
  import Select from "../components/Select";
  import PetEditor from "./PetEditor.svelte";
  import VisitTreatment from "./VisitTreatment.svelte";

  let allPet = [];
  let petOwnerId = null;
  let petId = undefined;
  function onPetClicked(pet) {
    petId = pet.id;
  }

  let petEditorCreate = false;
  let petEditorUpdate = false;
  $: petEditorDisabled = petEditorCreate || petEditorUpdate;
  function petEditorCreateClicked() {
    petEditorCreate = true;
  }
  function petEditorUpdateClicked(pet) {
    petId = pet.id;
    petEditorUpdate = true;
  }

  let visitEditorCreate = false;
  function visitEditorCreateClicked(pet) {
    petId = pet.id;
    visitEditorCreate = true;
  }

  let allOwnerItem = [];
  function ownerToOwnerItem(owner) {
    return {
      value: owner.id,
      text: owner.name + ", " + owner.address,
    };
  }

  let allSpeciesEnum = [];

  onMount(async () => {
    try {
      allOwnerItem = await loadAllValue("/api/owner?sort=name,asc");
      allOwnerItem = allOwnerItem.map(ownerToOwnerItem);
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
    }
    petOwnerId = $storedOwner.id;
  });

  $: (petOwnerId, reloadAllPet());
  function reloadAllPet() {
    console.log(petOwnerId);
    if (petOwnerId) {
      loadAllValue("/api/pet?owner.id=" + petOwnerId)
        .then((json) => {
          console.log(["reloadAllPet", json]);
          $storedOwner.id = petOwnerId;
          allPet = json;
        })
        .catch((err) => {
          console.log(["reloadAllPet", err]);
          toast.push(err.toString());
        });
    } else {
      allPet = [];
    }
  }
</script>

<h1>Pet</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <div class="flex-grow">
    <Select
      bind:value={petOwnerId}
      valueGetter={(v) => v?.value}
      allItem={allOwnerItem}
      disabled={petEditorDisabled}
      label="Owner"
      placeholder="Choose owner"
    />
  </div>
  <table class="table-fixed">
    <thead class="justify-between">
      <tr class="bg-gray-100">
        <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
          <span class="text-gray-600">Species</span>
        </th>
        <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/2">
          <span class="text-gray-600">Name</span>
        </th>
        <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
          <span class="text-gray-600">Born</span>
        </th>
        <th class="px-2 py-3 border-b-2 border-gray-300 w-16"> </th>
        <th class="px-2 py-3 border-b-2 border-gray-300 w-16">
          <Icon
            on:click={() => petEditorCreateClicked()}
            disabled={petEditorDisabled}
            name="edit"
            outlined
          />
        </th>
      </tr>
    </thead>
    <tbody>
      {#if petEditorCreate}
        <tr>
          <td class="px-4" colspan="4">
            <PetEditor
              bind:visible={petEditorCreate}
              on:create={(e) => reloadAllPet()}
              {allSpeciesEnum}
              ownerId={petOwnerId}
            />
          </td><td> </td></tr
        >
      {/if}
      {#each allPet as pet}
        <tr
          on:click={(e) => onPetClicked(pet)}
          title={pet.id}
          class:ring={petId === pet.id}
        >
          <td class="px-2 py-3 text-left">
            <span>{pet.species}</span>
          </td>
          <td class="px-2 py-3 text-left">
            <div class="text-sm underline text-blue-600">
              <a href={"/pet/" + pet.id}>{pet.name}</a>
            </div>
          </td>
          <td class="px-2 py-3 text-left">
            <span>{pet.born}</span>
          </td>
          <td class="px-2 py-3">
            <Icon
              on:click={() => visitEditorCreateClicked(pet)}
              title="Add a new visit"
              disabled={petEditorDisabled}
              name="event"
              outlined
            />
          </td>
          <td class="px-2 py-3">
            <Icon
              on:click={() => petEditorUpdateClicked(pet)}
              disabled={petEditorDisabled}
              name="edit"
              outlined
            />
          </td>
        </tr>
        {#if visitEditorCreate && petId === pet.id}
          <tr>
            <td class="px-4" colspan="6">
              <VisitTreatment
                bind:visible={visitEditorCreate}
                on:create={(e) => reloadAllPet()}
                {pet}
              />
            </td>
          </tr>
        {/if}
        {#if petEditorUpdate && petId === pet.id}
          <tr>
            <td class="px-4" colspan="4">
              <PetEditor
                bind:visible={petEditorUpdate}
                on:update={(e) => reloadAllPet()}
                on:remove={(e) => reloadAllPet()}
                {allSpeciesEnum}
                ownerId={pet.ownerItem.value}
                {pet}
              />
            </td><td> </td></tr
          >
        {/if}
      {:else}
        <tr>
          <td class="px-2 py-3" colspan="4"> No pets </td>
        </tr>
      {/each}
    </tbody>
  </table>
</div>
