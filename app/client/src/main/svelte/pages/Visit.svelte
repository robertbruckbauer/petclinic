<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import { mapify } from "../utils/list.js";
  import Icon from "../components/Icon";
  import Select from "../components/Select";
  import VisitDiagnose from "./VisitDiagnose.svelte";

  let allVetItem = [];
  let allSpeciesEnum = [];
  onMount(async () => {
    try {
      allVetItem = await loadAllValue("/api/vet?sort=name,asc");
      allVetItem = allVetItem.map((e) => ({}));
      console.log(["onMount", allVetItem]);
      allSpeciesEnum = await loadAllValue("/api/enum/species");
      allSpeciesEnum = allSpeciesEnum.map((e) => ({
        value: e.id,
        text: e.name,
      }));
      console.log(["onMount", allSpeciesEnum]);
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    }
    loadAllVisit();
  });

  let allVisit = [];
  let visitId = undefined;
  function onVisitClicked(_visit) {
    visitId = _visit.id;
  }
  function onVisitRemoveClicked(_visit) {
    visitId = _visit.id;
    removeVisit(_visit);
  }

  let visitEditorUpdate = false;
  $: visitEditorDisabled = visitEditorUpdate;
  function onVisitEditorUpdateClicked(_visit) {
    visitId = _visit.id;
    visitEditorUpdate = true;
  }

  let filterPrefix = null;
  $: allVisitFiltered = filterVisit(filterPrefix, allVisit);
  function filterVisit(prefix, allValue) {
    if (!filterPrefix) return allValue;
    return allValue.filter((e) => {
      for (const s of e.petItem.text.split(" ")) {
        if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
          return true;
        }
      }
      return false;
    });
  }

  $: allVisitByDate = mapify(allVisitFiltered, visitKey, visitCompare);
  function visitKey(e) {
    return e.date;
  }
  function visitCompare(e1, e2) {
    return e1.id.localeCompare(e2.id);
  }

  function loadAllVisit() {
    loadAllValue("/api/visit?sort=date,desc")
      .then((json) => {
        console.log(["loadAllVisit", json]);
        allVisit = json;
      })
      .catch((err) => {
        console.log(["loadAllVisit", err]);
        toast.push(err.toString());
      });
  }

  function removeVisit(_visit) {
    const text = _visit.date + " " + _visit.petItem.text;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Visit '" + hint + "' wirklich löschen?")) return;
    removeValue("/api/visit/" + _visit.id)
      .then((json) => {
        console.log(["removeVisit", _visit, json]);
        loadAllVisit();
      })
      .catch((err) => {
        console.log(["removeVisit", _visit, err]);
        toast.push(err.toString());
      });
  }
</script>

<h1>Visit</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <div class="flex-grow">
    <Select
      bind:value={filterPrefix}
      valueGetter={(v) => v?.value}
      allItem={allSpeciesEnum}
      disabled={visitEditorDisabled}
      nullable
      label="Filter"
      placeholder="Choose species"
    />
  </div>
  <div class="flex-grow">
    {#each [...allVisitByDate] as [date, allVisitOfDate], i}
      <h4>{date} <small>({allVisitOfDate.length})</small></h4>
      <table class="table-fixed">
        <thead class="justify-between">
          <tr class="bg-gray-100">
            <th class="px-2 py-3 text-left w-2/6 table-cell">
              <span class="text-gray-600">Owner</span>
            </th>
            <th class="px-2 py-3 text-left w-2/6 table-cell">
              <span class="text-gray-600">Pet</span>
            </th>
            <th class="px-2 py-3 text-left w-2/6 table-cell">
              <span class="text-gray-600">Vet</span>
            </th>
            <th class="px-2 py-3 border-b-2 border-gray-300 w-16"> </th>
          </tr>
        </thead>
        <tbody>
          {#each allVisitOfDate as visit}
            <tr
              on:click={(e) => onVisitClicked(visit)}
              title={visit.id}
              class:ring={visitId === visit.id}
            >
              <td class="px-2 py-3 text-left table-cell">
                {visit.ownerItem.text}
              </td>
              <td class="px-2 py-3 text-left table-cell">
                {visit.petItem.text}
              </td>
              <td class="px-2 py-3 text-left table-cell">
                {visit.vetItem.text}
              </td>
              <td class="px-2 py-3 table-cell">
                <div
                  class="grid grid-cols-1 md:grid-cols-2 items-center gap-1 w-max"
                >
                  <Icon
                    onclick={() => onVisitRemoveClicked(visit)}
                    title="Delete a visit"
                    disabled={visitEditorDisabled}
                    name="delete"
                    outlined
                  />
                  <Icon
                    onclick={() => onVisitEditorUpdateClicked(visit)}
                    title="Edit visit details"
                    disabled={visitEditorDisabled}
                    name="edit"
                    outlined
                  />
                </div>
              </td>
            </tr>
            {#if visitEditorUpdate && visitId === visit.id}
              <tr>
                <td class="border-l-4 px-4" colspan="4">
                  <VisitDiagnose
                    bind:visible={visitEditorUpdate}
                    onupdate={loadAllVisit}
                    {allVetItem}
                    {visit}
                  />
                </td>
              </tr>
            {/if}
          {:else}
            <tr>
              <td class="px-2 py-3" colspan="4">No visits</td>
            </tr>
          {/each}
        </tbody>
      </table>
    {:else}
      <span>No visits</span>
    {/each}
  </div>
</div>
