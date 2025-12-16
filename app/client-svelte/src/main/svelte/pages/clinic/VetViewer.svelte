<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { toast } from "../../components/Toast";
  import { EnumService } from "../../services/enum.service";
  import { VetService } from "../../services/vet.service";
  import type { EnumItem } from "../../types/enum.type";
  import type { Vet } from "../../types/vet.type";
  import VetEditor from "./VetEditor.svelte";

  const enumService = new EnumService();
  const vetService = new VetService();

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let allSkillEnum: EnumItem[] = $state([]);
  let vet = $state({} as Vet);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        allSkillEnum: enumService.loadAllEnum("skill"),
        vet: vetService.loadOneVet(id),
      }).subscribe({
        next: (value) => {
          allSkillEnum = value.allSkillEnum;
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

  function onCancel() {
    vet = { ...vet };
  }

  function onUpdate(newVet: Vet) {
    vet = newVet;
  }
</script>

<h1>{vet.name}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {/if}
  {#if vet.id}
    <VetEditor
      visible={true}
      oncancel={onCancel}
      onupdate={onUpdate}
      {allSkillEnum}
      {vet}
    />
  {/if}
</div>
