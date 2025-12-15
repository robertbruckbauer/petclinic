<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast";
  import { VetService } from "../../services/vet.service";
  import type { Vet } from "../../types/vet.type";

  const vetService = new VetService();

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let vet = $state({} as Vet);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        vet: vetService.loadOneVet(id),
      }).subscribe({
        next: (value) => {
          vet = value.vet;
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

<h1>{vet.name}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {/if}
</div>
