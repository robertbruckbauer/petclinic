<script lang="ts">
  import type { Visit } from "../../types/visit.type";
  import VisitCard from "./VisitCard.svelte";

  interface Props {
    allVisit: Visit[];
  }

  let { allVisit }: Props = $props();
</script>

<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 items-center gap-1">
  {#each allVisit as visit, i}
    <VisitCard {visit} />
  {:else}
    <span>No visits</span>
  {/each}
</div>
