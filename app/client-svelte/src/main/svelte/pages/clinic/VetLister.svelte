<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast/index.js";
  import { EnumService } from "../../services/enum.service";
  import { VetService } from "../../services/vet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Vet } from "../../types/vet.type";
  import VetEditor from "./VetEditor.svelte";

  const enumService = new EnumService();
  const vetService = new VetService();

  let allSkillEnum: EnumItem[] = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        allSkillEnum: enumService.loadAllEnum("skill"),
      }).subscribe({
        next: (value) => {
          allSkillEnum = value.allSkillEnum;
        },
        error: (err) => {
          toast.push(err);
        },
      });
      loadAllVet();
    } finally {
      loading = false;
    }
  });

  let vetId = $state("");
  function onVetClicked(_vet: Vet) {
    vetId = _vet.id!;
  }
  function onVetRemoveClicked(_vet: Vet) {
    vetId = _vet.id!;
    removeVet(_vet);
  }

  let vetEditorCreate = $state(false);
  function onVetEditorCreateClicked() {
    vetId = "";
    vetEditorCreate = true;
    vetEditorUpdate = false;
  }

  let vetEditorUpdate = $state(false);
  function onVetEditorUpdateClicked(_vet: Vet) {
    vetId = _vet.id!;
    vetEditorUpdate = true;
    vetEditorCreate = false;
  }

  const vetFilterDisabled = $derived(vetEditorCreate || vetEditorUpdate);

  const vetEditorDisabled = $derived(vetFilterDisabled);

  function onFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllVet();
    } finally {
      loading = false;
    }
  }

  let allVet: Vet[] = $state([]);
  function afterCreateVet(_vet: Vet) {
    allVet = [_vet, ...allVet];
  }
  function afterUpdateVet(_vet: Vet) {
    allVet = allVet.map((e) => (e.id === _vet.id ? _vet : e));
  }
  function afterRemoveVet(_vet: Vet) {
    allVet = allVet.filter((e) => e.id !== _vet.id);
  }

  let vetFilter = $state("");
  function loadAllVet() {
    const search = { sort: "name,asc", name: vetFilter ? vetFilter : "%" };
    vetService.loadAllVet(search).subscribe({
      next: (json) => {
        allVet = json;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function removeVet(_vet: Vet) {
    const text = _vet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete vet '" + hint + "' permanently?")) return;
    vetService.removeVet(_vet.id!).subscribe({
      next: (json) => {
        afterRemoveVet(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }
</script>

<h1>Vet</h1>

<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onFilterClicked}>
    <div class="flex flex-row gap-2 items-center pb-2 pr-2">
      <input
        bind:value={vetFilter}
        aria-label="Filter"
        type="text"
        class="input input-bordered w-full"
        readonly={vetFilterDisabled}
        placeholder="Enter filter critria"
      />
      <button
        type="submit"
        title="Filter items"
        class="btn btn-circle btn-outline"
        disabled={vetFilterDisabled}
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
            <span class="text-gray-600">Skills</span>
          </th>
          <th class="px-2 py-3 text-right w-0 table-cell">
            <button
              title="Add a new vet"
              class="btn btn-circle btn-outline"
              onclick={onVetEditorCreateClicked}
              disabled={vetEditorDisabled}
            >
              <span class="material-icons">add</span>
            </button>
          </th>
        </tr>
      </thead>
      <tbody>
        {#if vetEditorCreate}
          {@const vet: Vet = {
              version: 0,
              name: "",
              allSkill: [],
            }}
          <tr>
            <td class="px-2" colspan="3">
              <VetEditor
                oncreate={afterCreateVet}
                bind:visible={vetEditorCreate}
                {allSkillEnum}
                {vet}
              />
            </td>
          </tr>
        {/if}
        {#each allVet as vet, i}
          <tr
            onclick={(e) => onVetClicked(vet)}
            title={vet.id}
            class:border-l-2={vetId === vet.id}
            class:bg-gray-100={i % 2 === 1}
          >
            <td class="px-2 py-3 text-left table-cell">
              <span>{vet.name}</span>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              {#each vet.allSkill as skill}
                <div class="flex flex-col">
                  <span>{skill}</span>
                </div>
              {:else}
                <span>No skills</span>
              {/each}
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-2 items-center gap-1 w-max"
              >
                <button
                  title="Delete a vet"
                  class="btn btn-circle btn-outline"
                  onclick={() => onVetRemoveClicked(vet)}
                  disabled={vetEditorDisabled}
                >
                  <span class="material-icons">delete</span>
                </button>
                <button
                  title="Edit a vet"
                  class="btn btn-circle btn-outline"
                  onclick={() => onVetEditorUpdateClicked(vet)}
                  disabled={vetEditorDisabled}
                >
                  <span class="material-icons">edit</span>
                </button>
              </div>
            </td>
          </tr>
          {#if vetEditorUpdate && vetId === vet.id}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <VetEditor
                  onupdate={afterUpdateVet}
                  bind:visible={vetEditorUpdate}
                  {allSkillEnum}
                  {vet}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="3">No vets</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
