<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast";
  import { VisitService } from "../../services/visit.service";
  import type { Owner } from "../../types/owner.type";
  import type { Visit } from "../../types/visit.type";

  const visitService = new VisitService();

  interface Props {
    owner: Owner;
  }

  let { owner }: Props = $props();

  let allVisit: Visit[] = $state([]);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      const search = { sort: "date,desc", "pet.owner.id": owner.id! };
      forkJoin({
        allVisit: visitService.loadAllVisit(search),
      }).subscribe({
        next: (value) => {
          allVisit = value.allVisit;
        },
        error: (err) => {
          toast.push(err);
        },
      });
    } finally {
      loading = false;
    }
  });
</script>

{#if loading}
  <div class="h-screen flex justify-center items-start">
    <span class="loading loading-spinner loading-xl"></span>
  </div>
{:else}
  <div
    class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 items-center gap-1"
  >
    {#each allVisit as visit}
      <div class="card flex flex-col border rounded-md p-2">
        <div class="flex flex-row gap-1">
          <fieldset class="fieldset w-full">
            <legend class="fieldset-legend">Pet</legend>
            <input
              aria-label="Pet"
              type="text"
              class="input w-full"
              readonly
              value={visit.petItem?.text}
            />
          </fieldset>
          <fieldset class="fieldset w-48">
            <legend class="fieldset-legend">Treatment</legend>
            <input
              aria-label="Treatment"
              type="date"
              class="input w-full"
              readonly
              value={visit.date}
            />
          </fieldset>
        </div>
        <fieldset class="fieldset w-full">
          <legend class="fieldset-legend">Vet</legend>
          <input
            aria-label="Vet"
            type="text"
            class="input w-full"
            readonly
            value={visit.vetItem?.text}
          />
        </fieldset>
        <fieldset class="fieldset w-full">
          <legend class="fieldset-legend">Diagnose</legend>
          <textarea aria-label="Diagnose" class="textarea w-full" readonly>
            {visit.text}</textarea
          >
        </fieldset>
        <a class="btn" href={"/visit/" + visit.id}>Edit</a>
      </div>
    {:else}
      <span>No visits</span>
    {/each}
  </div>
{/if}
