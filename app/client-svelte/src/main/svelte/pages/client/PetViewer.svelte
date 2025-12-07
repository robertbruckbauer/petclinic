<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { PetService } from "../../services/pet.service";
  import type { Pet } from "../../types/pet.type";
  import { toast } from "../../components/Toast";
  import Circle from "../../components/Spinner";

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let loading = $state(true);

  const petService = new PetService();

  let pet = $state({} as Pet);

  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        pet: petService.loadOnePet(id),
      }).subscribe({
        next: (value) => {
          pet = value.pet;
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

<h1>{pet.name}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
    </div>
  {/if}
</div>
