<script>
  export let open = false;
</script>

<a
  class="text-title-500 hover:text-title-700 cursor-pointer mr-4 border-none focus:outline-none"
  class:open
  href="/home"
  on:click={() => (open = false)}
>
  <span>Petclinic</span>
</a>
