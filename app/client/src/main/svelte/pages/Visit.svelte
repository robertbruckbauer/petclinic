<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { mapify } from "../utils/list.js";
  import Icon from "../components/Icon";
  import Select from "../components/Select";
  import VisitDiagnose from "./VisitDiagnose.svelte";

  let allVisit = [];
  let visitId = undefined;
  function onVisitClicked(visit) {
    visitId = visit.id;
  }

  let visitEditorUpdate = false;
  $: visitEditorDisabled = visitEditorUpdate;
  function visitEditorUpdateClicked(visit) {
    visitId = visit.id;
    visitEditorUpdate = true;
  }

  let allVetItem = [];
  function vetToVetItem(vet) {
    return {
      value: vet.id,
      text: vet.name,
    };
  }

  let allSpeciesEnum = [];

  onMount(async () => {
    try {
      allVetItem = await loadAllValue("/api/vet?sort=name,asc");
      allVetItem = allVetItem.map(vetToVetItem);
      console.log(["onMount", allVetItem]);
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
    reloadAllVisit();
  });

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

  function reloadAllVisit() {
    loadAllValue("/api/visit?sort=date,desc")
      .then((json) => {
        console.log(["reloadAllVisit", json]);
        allVisit = json;
      })
      .catch((err) => {
        console.log(["reloadAllVisit", err]);
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
            <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/6">
              <span class="text-gray-600">Owner</span>
            </th>
            <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/6">
              <span class="text-gray-600">Pet</span>
            </th>
            <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/6">
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
              <td class="px-2 py-3 text-left">
                {visit.ownerItem.text}
              </td>
              <td class="px-2 py-3 text-left">
                {visit.petItem.text}
              </td>
              <td class="px-2 py-3 text-left">
                {visit.vetItem.text}
              </td>
              <td class="px-2 py-3">
                <Icon
                  on:click={() => visitEditorUpdateClicked(visit)}
                  title="Edit visit details"
                  disabled={visitEditorDisabled}
                  name="edit"
                  outlined
                />
              </td>
            </tr>
            {#if visitEditorUpdate && visitId === visit.id}
              <tr>
                <td class="px-4" colspan="4">
                  <VisitDiagnose
                    bind:visible={visitEditorUpdate}
                    on:update={(e) => reloadAllVisit()}
                    on:remove={(e) => reloadAllVisit()}
                    {date}
                    {visit}
                    {allVetItem}
                  />
                </td><td> </td></tr
              >
            {/if}
          {:else}
            <tr>
              <td class="px-2 py-3" colspan="4"> No visits </td>
            </tr>
          {/each}
        </tbody>
      </table>
    {:else}
      No visits
    {/each}
  </div>
</div>
