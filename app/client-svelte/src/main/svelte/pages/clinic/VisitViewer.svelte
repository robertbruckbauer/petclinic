<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../controls/Toast";
  import { VisitService } from "../../services/visit.service";
  import type { Visit } from "../../types/visit.type";
  import type { Vet } from "../../types/vet.type";

  const visitService = new VisitService();

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let visit = $state({} as Visit);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        visit: visitService.loadOneVisit(id),
      }).subscribe({
        next: (value) => {
          visit = value.visit;
        },
        error: (err) => {
          toast.push(err);
        },
      });
    } finally {
      loading = false;
    }
  });

  function formatDuration(iso: string | undefined): string {
    if (!iso) return "-";
    const match = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?$/.exec(iso);
    if (!match) return iso;
    const parts: string[] = [];
    if (match[1])
      parts.push(match[1] + (Number(match[1]) === 1 ? " hour" : " hours"));
    if (match[2])
      parts.push(match[2] + (Number(match[2]) === 1 ? " minute" : " minutes"));
    if (match[3])
      parts.push(match[3] + (Number(match[3]) === 1 ? " second" : " seconds"));
    return parts.length ? parts.join(" ") : "-";
  }
</script>

<h1>Visit of {visit?.petItem?.text + " on " + visit.date}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {/if}
  {#if visit.id}
    <div class="flex flex-col sm:flex-row gap-2 pt-4">
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Date</legend>
        <p class="p-2">{visit.date}</p>
      </fieldset>
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Time</legend>
        <p class="p-2">{visit.time ?? "-"}</p>
      </fieldset>
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Duration</legend>
        <p class="p-2">{formatDuration(visit.duration)}</p>
      </fieldset>
    </div>
  {/if}
</div>
