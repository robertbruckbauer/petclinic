<script>
  import VisitCard from "./VisitCard.svelte";
  let { allVisit } = $props();
</script>

<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 items-center gap-1">
  {#each allVisit as visit, i}
    <VisitCard {visit} />
  {:else}
    <span>No visits</span>
  {/each}
</div>
