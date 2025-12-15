<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { OwnerService } from "../../services/owner.service";
  import type { Owner } from "../../types/owner.type";
  import { toast } from "../../components/Toast";
  import OwnerEditor from "./OwnerEditor.svelte";

  const ownerService = new OwnerService();

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let owner = $state({} as Owner);
  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      forkJoin({
        pet: ownerService.loadOneOwner(id),
      }).subscribe({
        next: (value) => {
          owner = value.pet;
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
    owner = { ...owner };
  }

  function onUpdate(newOwner: Owner) {
    owner = newOwner;
  }
</script>

<h1>{owner.name}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {:else}
    <OwnerEditor
      visible={true}
      oncancel={onCancel}
      onupdate={onUpdate}
      {owner}
    />
  {/if}
</div>
