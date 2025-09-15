<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { updatePatch } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import Circle from "../components/Spinner";
  import Icon from "../components/Icon";
  import TextField from "../components/TextField";
  import VetEditor from "./VetEditor.svelte";

  let allSkillEnum = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      allSkillEnum = await loadAllValue("/api/enum/skill");
      allSkillEnum = allSkillEnum.map((e) => ({
        value: e.value,
        text: e.name,
      }));
      loadAllVet();
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    } finally {
      loading = false;
    }
  });

  let vetId = $state();
  function onVetClicked(_vet) {
    vetId = _vet.id;
  }
  function onVetRemoveClicked(_vet) {
    vetId = _vet.id;
    removeVet(_vet);
  }

  let vetEditorCreate = $state(false);
  function onVetEditorCreateClicked() {
    vetEditorCreate = true;
    vetEditorUpdate = false;
  }

  let vetEditorUpdate = $state(false);
  function onVetEditorUpdateClicked(_vet) {
    vetId = _vet.id;
    vetEditorUpdate = true;
    vetEditorCreate = false;
  }

  let vetEditorDisabled = $derived(vetEditorCreate || vetEditorUpdate);

  let vetFilter = $state("");
  function vetFilterParameter() {
    if (!vetFilter) return "";
    return "&name=" + encodeURIComponent(vetFilter);
  }
  function vetSortParameter() {
    return "?sort=name";
  }

  function onVetFilterClicked(_event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllVet();
    } finally {
      loading = false;
    }
  }

  let allVet = $state([]);
  function onCreateVet(_vet) {
    allVet = allVet.toSpliced(0, 0, _vet);
  }
  function onUpdateVet(_vet) {
    let index = allVet.findIndex((e) => e.id === _vet.id);
    if (index > -1) allVet = allVet.toSpliced(index, 1, _vet);
  }
  function onRemoveVet(_vet) {
    let index = allVet.findIndex((e) => e.id === _vet.id);
    if (index > -1) allVet = allVet.toSpliced(index, 1);
  }

  function loadAllVet() {
    const query = vetSortParameter() + vetFilterParameter();
    loadAllValue("/api/vet" + query)
      .then((json) => {
        const msg = import.meta.env.DEV ? json : json.length;
        console.log(["loadAllVet", query, msg]);
        allVet = json;
      })
      .catch((err) => {
        console.log(["loadAllVet", query, err]);
        toast.push(err.toString());
      });
  }

  function updateVet(_vet) {
    updatePatch("/api/vet/" + _vet.id, _vet)
      .then((json) => {
        console.log(["updateVet", _vet, json]);
        onUpdateVet(json);
      })
      .catch((err) => {
        console.log(["updateVet", _vet, err]);
        toast.push(err.toString());
      });
  }

  function removeVet(_vet) {
    const text = _vet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete vet '" + hint + "' permanently?")) return;
    removeValue("/api/vet/" + _vet.id)
      .then((json) => {
        console.log(["removeVet", _vet, json]);
        onRemoveVet(json);
      })
      .catch((err) => {
        console.log(["removeVet", _vet, err]);
        toast.push(err.toString());
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
        <tr class="bg-title-100">
          <th class="px-2 py-3 text-left w-1/3 table-cell">
            <span class="text-title-600">Name</span>
          </th>
          <th class="px-2 py-3 text-left w-full table-cell">
            <span class="text-title-600">Skills</span>
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
          <tr>
            <td class="px-2" colspan="3">
              <VetEditor
                bind:visible={vetEditorCreate}
                oncreate={onCreateVet}
                {allSkillEnum}
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
            <td class="px-2" colspan="3">Keine Vetn</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
