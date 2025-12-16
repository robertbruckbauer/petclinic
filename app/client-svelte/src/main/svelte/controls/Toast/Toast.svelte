<script lang="ts">
  import { fade, fly } from "svelte/transition";
  import { toast } from "./stores.js";
  import ToastItem from "./ToastItem.svelte";
</script>

<ul class="_toastContainer">
  {#each $toast as item (item.id)}
    <li in:fly={{ x: 256 }} out:fade>
      <ToastItem {item} />
    </li>
  {/each}
</ul>

<style>
  ._toastContainer {
    top: 1.5rem;
    right: 2rem;
    bottom: auto;
    left: auto;
    position: fixed;
    margin: 0;
    padding: 0;
    list-style-type: none;
    pointer-events: none;
    z-index: 99;
  }
</style>
