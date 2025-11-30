<script>
  import { VisitService } from "../../services/visit.service";
  import { mapVetToVetItem, VetService } from "../../services/vet.service";
  import { EnumService } from "../../services/enum.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";
  import Circle from "../../components/Spinner";
  import Icon from "../../components/Icon";
  import VisitDiagnose from "./VisitDiagnose.svelte";

  const visitService = new VisitService();
  const vetService = new VetService();
  const enumService = new EnumService();

  let allVetItem = $state([]);
  let allSpeciesEnum = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      vetService.loadAllVet("?sort=name,asc").subscribe({
        next: (json) => {
          allVetItem = json.map(mapVetToVetItem);
        },
        error: (err) => {
          toast.push(err.detail || err.toString());
        },
      });
      enumService.loadAllEnum("species").subscribe({
        next: (json) => {
          allSpeciesEnum = json.map((e) => ({
            value: e.id,
            text: e.name,
          }));
        },
        error: (err) => {
          toast.push(err.detail || err.toString());
        },
      });
      loadAllVisit();
    } finally {
      loading = false;
    }
  });

  let visitId = $state();
  function onVisitClicked(_visit) {
    visitId = _visit.id;
  }
  function onVisitRemoveClicked(_visit) {
    visitId = _visit.id;
    removeVisit(_visit);
  }

  let visitEditorUpdate = $state(false);
  function onVisitEditorUpdateClicked(_visit) {
    visitId = _visit.id;
    visitEditorUpdate = true;
  }

  const visitEditorDisabled = $derived(visitEditorUpdate);

  let allVisit = $state([]);
  function onCreateVisit(_visit) {
    allVisit = allVisit.toSpliced(0, 0, _visit).sort(dateComparator);
  }
  function onUpdateVisit(_visit) {
    let index = allVisit.findIndex((e) => e.id === _visit.id);
    if (index > -1) {
      allVisit = allVisit.toSpliced(index, 1, _visit).sort(dateComparator);
    }
  }
  function onRemoveVisit(_visit) {
    let index = allVisit.findIndex((e) => e.id === _visit.id);
    if (index > -1) {
      allVisit = allVisit.toSpliced(index, 1);
    }
  }

  /**
   * Start swimlane if the date changes
   * in a date ordered array.
   *
   * @param _index from #each
   */
  function isSwimlaneChange(_index) {
    if (_index) {
      return allVisit[_index - 1].date !== allVisit[_index].date;
    } else {
      return true;
    }
  }

  /**
   * Comparator for a date ordered array.
   */
  const dateComparator = (e1, e2) => e1.date.localeCompare(e2.date);

  function loadAllVisit() {
    visitService.loadAllVisit("?sort=date,desc").subscribe({
      next: (json) => {
        allVisit = json.sort(dateComparator);
      },
      error: (err) => {
        toast.push(err.detail || err.toString());
      },
    });
  }

  function removeVisit(_visit) {
    const hint = _visit.date;
    if (!confirm("Delete visit at '" + hint + "' permanently?")) return;
    visitService.removeVisit(_visit.id).subscribe({
      next: (json) => {
        loadAllVisit();
      },
      error: (err) => {
        toast.push(err.detail || err.toString());
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
  {/if}
</div>
