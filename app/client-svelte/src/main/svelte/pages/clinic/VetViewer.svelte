<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { VetService } from "../../services/vet.service";
  import type { Vet } from "../../types/vet.type";
  import { toast } from "../../components/Toast";
  import Circle from "../../components/Spinner";

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let loading = $state(true);

  const vetService = new VetService();

  let vet = $state({} as Vet);

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
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
    </div>
  {/if}
</div>
