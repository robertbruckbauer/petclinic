<script>
  let { open = $bindable() } = $props();
  function handleClick() {
    open = false;
  }
</script>

<a
  aria-label="Logo"
  class="text-title-500 hover:text-title-700 cursor-pointer mr-4 border-none focus:outline-none p-0 flex items-center justify-center"
  class:open
  href="/home"
  onclick={handleClick}
>
  <span>Petclinic</span>
</a>
