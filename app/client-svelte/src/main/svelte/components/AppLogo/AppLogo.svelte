<script>
  let { open = $bindable() } = $props();
  function handleClick() {
    open = false;
  }
</script>

<a
  aria-label="Menü"
  class="text-gray-500 hover:text-gray-700 cursor-pointer mr-4 border-none focus:outline-none"
  class:open
  href="/home"
  onclick={handleClick}
>
  <span>Petclinic</span>
</a>
