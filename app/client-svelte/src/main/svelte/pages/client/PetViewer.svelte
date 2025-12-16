<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast";
  import { EnumService } from "../../services/enum.service";
  import { PetService } from "../../services/pet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Pet } from "../../types/pet.type";
  import PetEditor from "./PetEditor.svelte";

  const enumService = new EnumService();
  const petService = new PetService();

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let allSpeciesEnum: EnumItem[] = $state([]);
  let pet = $state({} as Pet);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        allSpeciesEnum: enumService.loadAllEnum("species"),
        pet: petService.loadOnePet(id),
      }).subscribe({
        next: (value) => {
          allSpeciesEnum = value.allSpeciesEnum;
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

  function onCancel() {
    pet = { ...pet };
  }

  function onUpdate(newPet: Pet) {
    pet = newPet;
  }
</script>

<h1>{pet.name}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {/if}
  {#if pet.id}
    <PetEditor
      visible={true}
      oncancel={onCancel}
      onupdate={onUpdate}
      {allSpeciesEnum}
      {pet}
    />
  {/if}
</div>
