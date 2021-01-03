<script>
  import { fade, fly } from "svelte/transition";
  import { flip } from "svelte/animate";
  import { toast } from "./stores.js";
  import ToastItem from "./ToastItem.svelte";
  export let options = {};
  export let target = "default";
  $: toast._init(target, options);
  $: allItem = $toast.filter((i) => i.target === target);
</script>

<ul class="_toastContainer">
  {#each allItem as item (item.id)}
    <li in:fly={{ x: 256 }} out:fade animate:flip={{ duration: 200 }}>
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
