<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast";
  import { EnumService } from "../../services/enum.service";
  import { VetService } from "../../services/vet.service";
  import { VisitService } from "../../services/visit.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { VetItem } from "../../types/vet.type";
  import type { Visit } from "../../types/visit.type";
  import VisitDiagnose from "./VisitDiagnose.svelte";

  const visitService = new VisitService();
  const vetService = new VetService();
  const enumService = new EnumService();

  let allVetItem: VetItem[] = $state([]);
  let allSpeciesEnum: EnumItem[] = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        allVetItem: vetService.loadAllVetItem(),
        allSpeciesEnum: enumService.loadAllEnum("species"),
      }).subscribe({
        next: (value) => {
          allVetItem = value.allVetItem;
          allSpeciesEnum = value.allSpeciesEnum;
        },
        error: (err) => {
          toast.push(err);
        },
      });
      loadAllVisit();
    } finally {
      loading = false;
    }
  });

  let visitId = $state("");
  function onVisitClicked(_visit: Visit) {
    visitId = _visit.id!;
  }
  function onVisitRemoveClicked(_visit: Visit) {
    visitId = _visit.id!;
    removeVisit(_visit);
  }

  let visitEditorUpdate = $state(false);
  function onVisitEditorUpdateClicked(_visit: Visit) {
    visitId = _visit.id!;
    visitEditorUpdate = true;
  }

  const visitEditorDisabled = $derived(visitEditorUpdate);

  let allVisit: Visit[] = $state([]);
  function afterCreateVisit(_visit: Visit) {
    allVisit = [_visit, ...allVisit];
  }
  function afterUpdateVisit(_visit: Visit) {
    allVisit = allVisit.map((e) => (e.id === _visit.id ? _visit : e));
  }
  function afterRemoveVisit(_visit: Visit) {
    allVisit = allVisit.filter((e) => e.id !== _visit.id);
  }

  /**
   * Start swimlane if the date changes
   * in a date ordered array.
   *
   * @param _index from #each
   */
  function isSwimlaneChange(_index: number) {
    if (_index) {
      return allVisit[_index - 1].date !== allVisit[_index].date;
    } else {
      return true;
    }
  }

  /**
   * Comparator for a date ordered swimlanes.
   */
  const dateComparator = (e1: Visit, e2: Visit) =>
    e1.date.localeCompare(e2.date);

  function loadAllVisit() {
    const search = { sort: "date,desc" };
    visitService.loadAllVisit(search).subscribe({
      next: (json) => {
        allVisit = json.sort(dateComparator);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function removeVisit(_visit: Visit) {
    const hint = _visit.date;
    if (!confirm("Delete visit at '" + hint + "' permanently?")) return;
    visitService.removeVisit(_visit.id!).subscribe({
      next: (json) => {
        afterRemoveVisit(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }
</script>

<h1>Visit</h1>

<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {:else}
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-200">
          <th class="px-2 py-3 text-left w-2/6 table-cell">
            <span class="text-gray-600">Owner</span>
          </th>
          <th class="px-2 py-3 text-left w-2/6 table-cell">
            <span class="text-gray-600">Pet</span>
          </th>
          <th class="px-2 py-3 text-left w-2/6 table-cell">
            <span class="text-gray-600">Vet</span>
          </th>
          <th class="px-2 py-3 w-16"></th>
        </tr>
      </thead>
      <tbody>
        {#each allVisit as visit, i}
          {#if isSwimlaneChange(i)}
            <tr class="bg-gray-100">
              <td class="px-2 py-3" colspan="4">
                <span class="h-5 text-xl">{visit.date}</span>
              </td>
            </tr>
          {/if}
          <tr
            onclick={(e) => onVisitClicked(visit)}
            title={visit.id}
            class:border-l-2={visitId === visit.id}
          >
            <td class="px-2 py-3 text-left table-cell">
              <a href={"/owner/" + visit.ownerItem?.value}
                >{visit.ownerItem?.text}</a
              >
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <a href={"/visit/" + visit.id}>{visit.petItem?.text}</a>
            </td>
            <td class="px-2 py-3 text-left table-cell">
              <a href={"/vet/" + visit.vetItem?.value}>{visit.vetItem?.text}</a>
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-2 items-center gap-1 w-max"
              >
                <button
                  title="Delete a visit"
                  class="btn btn-circle btn-outline"
                  onclick={() => onVisitRemoveClicked(visit)}
                  disabled={visitEditorDisabled}
                >
                  <span class="material-icons">delete</span>
                </button>
                <button
                  title="Edit visit details"
                  class="btn btn-circle btn-outline"
                  onclick={() => onVisitEditorUpdateClicked(visit)}
                  disabled={visitEditorDisabled}
                >
                  <span class="material-icons">edit</span>
                </button>
              </div>
            </td>
          </tr>
          {#if visitEditorUpdate && visitId === visit.id}
            <tr>
              <td class="border-l-4 px-4" colspan="4">
                <VisitDiagnose
                  onupdate={afterUpdateVisit}
                  bind:visible={visitEditorUpdate}
                  {allVetItem}
                  {visit}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="3">No visits</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
