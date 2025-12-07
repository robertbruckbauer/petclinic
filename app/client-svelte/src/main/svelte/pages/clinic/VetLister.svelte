<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { EnumService } from "../../services/enum.service";
  import { VetService } from "../../services/vet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Vet } from "../../types/vet.type";
  import { toast } from "../../components/Toast/index.js";
  import Circle from "../../components/Spinner/index.js";
  import Icon from "../../components/Icon/index.js";
  import TextField from "../../components/TextField/index.js";
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

  let vetId = $state();
  function onVetClicked(_vet: Vet) {
    vetId = _vet.id;
  }
  function onVetRemoveClicked(_vet: Vet) {
    vetId = _vet.id;
    removeVet(_vet);
  }

  let vetEditorCreate = $state(false);
  function onVetEditorCreateClicked() {
    vetEditorCreate = true;
    vetEditorUpdate = false;
  }

  let vetEditorUpdate = $state(false);
  function onVetEditorUpdateClicked(_vet: Vet) {
    vetId = _vet.id;
    vetEditorUpdate = true;
    vetEditorCreate = false;
  }

  const vetEditorDisabled = $derived(vetEditorCreate || vetEditorUpdate);

  function onVetFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllVet();
    } finally {
      loading = false;
    }
  }

  let allVet: Vet[] = $state([]);
  function onCreateVet(_vet: Vet) {
    allVet = [_vet, ...allVet];
  }
  function onUpdateVet(_vet: Vet) {
    allVet = allVet.map((e) => (e.id === _vet.id ? _vet : e));
  }
  function onRemoveVet(_vet: Vet) {
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
        onRemoveVet(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }
</script>

<h1 title="Liste der Vetn, ggfs. gefiltert, jedes Element editierbar">Vet</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onVetFilterClicked}>
    <div class="flex flex-row gap-1 items-center pr-2">
      <div class="w-full">
        <TextField
          bind:value={vetFilter}
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
            <span class="text-gray-600">Skills</span>
          </th>
          <th class="px-2 py-3 text-right w-0 table-cell">
            <Icon
              onclick={() => onVetEditorCreateClicked()}
              disabled={vetEditorDisabled}
              title="Vet hinzufügen"
              name="add"
              outlined
            />
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
                bind:visible={vetEditorCreate}
                oncreate={onCreateVet}
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
              <div class="text-sm underline text-blue-600">
                <a href={"/vet/" + vet.id}>{vet.name}</a>
              </div>
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
                <Icon
                  onclick={() => onVetRemoveClicked(vet)}
                  disabled={vetEditorDisabled}
                  title="Vet löschen"
                  name="delete"
                  outlined
                />
                <Icon
                  onclick={() => onVetEditorUpdateClicked(vet)}
                  disabled={vetEditorDisabled}
                  title="Vet bearbeiten"
                  name="edit"
                  outlined
                />
              </div>
            </td>
          </tr>
          {#if vetEditorUpdate && vetId === vet.id}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <VetEditor
                  bind:visible={vetEditorUpdate}
                  onupdate={onUpdateVet}
                  {allSkillEnum}
                  {vet}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="3">Keine Vet</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
