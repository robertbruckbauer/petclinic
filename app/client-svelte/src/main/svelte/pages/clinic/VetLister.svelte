<script>
  import { VetService } from "../../services/vet.service";
  import { EnumService } from "../../services/enum.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast/index.js";
  import Circle from "../../components/Spinner/index.js";
  import Icon from "../../components/Icon/index.js";
  import TextField from "../../components/TextField/index.js";
  import VetEditor from "./VetEditor.svelte";

  const vetService = new VetService();
  const enumService = new EnumService();

  let allSkillEnum = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      enumService.loadAllEnum("skill").subscribe({
        next: (json) => {
          allSkillEnum = json.map((e) => ({
            value: e.value,
            text: e.name,
          }));
          loadAllVet();
        },
        error: (err) => {
          toast.push(err);
        },
      });
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

  const vetEditorDisabled = $derived(vetEditorCreate || vetEditorUpdate);

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
    vetService.loadAllVet(query).subscribe({
      next: (json) => {
        allVet = json;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function updateVet(_vet) {
    vetService.updateVet(_vet).subscribe({
      next: (json) => {
        onUpdateVet(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function removeVet(_vet) {
    const text = _vet.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Delete vet '" + hint + "' permanently?")) return;
    vetService.removeVet(_vet.id).subscribe({
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
