<script lang="ts">
  import { onMount } from "svelte";
  import { EnumService } from "../../services/enum.service";
  import { VetService } from "../../services/vet.service";
  import { VisitService } from "../../services/visit.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { VetItem } from "../../types/vet.type";
  import type { Visit } from "../../types/visit.type";
  import { toast } from "../../components/Toast";
  import Circle from "../../components/Spinner";
  import Icon from "../../components/Icon";
  import VisitDiagnose from "./VisitDiagnose.svelte";
  import { forkJoin } from "rxjs";

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

  let visitId: string | undefined = $state();
  function onVisitClicked(_visit: Visit) {
    visitId = _visit.id;
  }
  function onVisitRemoveClicked(_visit: Visit) {
    visitId = _visit.id;
    removeVisit(_visit);
  }

  let visitEditorUpdate = $state(false);
  function onVisitEditorUpdateClicked(_visit: Visit) {
    visitId = _visit.id;
    visitEditorUpdate = true;
  }

  const visitEditorDisabled = $derived(visitEditorUpdate);

  let allVisit: Visit[] = $state([]);
  function onCreateVisit(_visit: Visit) {
    allVisit = [_visit, ...allVisit];
  }
  function onUpdateVisit(_visit: Visit) {
    allVisit = allVisit.map((e) => (e.id === _visit.id ? _visit : e));
  }
  function onRemoveVisit(_visit: Visit) {
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
        loadAllVisit();
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
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
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
          <th class="px-2 py-3 w-16"> </th>
        </tr>
      </thead>
      <tbody>
        {#each allVisit as visit, i}
          {#if isSwimlaneChange(i)}
            <tr class="bg-gray-100">
              <td class="px-2 py-3" colspan="4">
                <span class="h-5 text-center text-xl">{visit.date}</span>
              </td>
            </tr>
          {/if}
          <tr
            onclick={(e) => onVisitClicked(visit)}
            title={visit.id}
            class:border-l-2={visitId === visit.id}
          >
            <td class="px-2 py-3 text-left table-cell">
              {visit.ownerItem!.text}
            </td>
            <td class="px-2 py-3 text-left table-cell">
              {visit.petItem!.text}
            </td>
            <td class="px-2 py-3 text-left table-cell">
              {visit.vetItem!.text}
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
  {/if}
</div>
