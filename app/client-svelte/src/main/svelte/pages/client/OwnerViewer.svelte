<script lang="ts">
  import { onMount } from "svelte";
  import { forkJoin } from "rxjs";
  import { OwnerService } from "../../services/owner.service";
  import type { Owner } from "../../types/owner.type";
  import { toast } from "../../components/Toast";
  import Circle from "../../components/Spinner";
  import OwnerEditor from "./OwnerEditor.svelte";

  interface Props {
    id: string;
  }

  let { id }: Props = $props();

  let loading = $state(true);

  const ownerService = new OwnerService();

  let owner = $state({} as Owner);

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
    <div class="h-screen flex justify-center items-center">
      <Circle size="60" unit="px" duration="1s" />
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
