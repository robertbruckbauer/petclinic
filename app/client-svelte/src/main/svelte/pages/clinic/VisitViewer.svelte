<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast";
  import { VisitService } from "../../services/visit.service";
  import type { Visit } from "../../types/visit.type";

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
</script>

<h1>{visit?.petItem?.text + " on " + visit.date}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {/if}
</div>
