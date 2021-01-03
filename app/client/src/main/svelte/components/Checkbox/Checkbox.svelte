<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'checked',
    'disabled',
    'label',
    'title'
  ], $$props);
  export let checked;
  export let disabled = false;
  export let label = undefined;
  export let title = undefined;
</script>

<div class="relative w-auto h-auto">
  <label {title} class="inline-flex items-center p-2 cursor-pointer">
    <input
      {...props}
      {title}
      class="disabled:opacity-50 border-2 border-primary-500 text-primary-500"
      type=checkbox 
      {disabled} 
      bind:checked 
      on:change/>
    {#if label}
    <div class="pl-2 cursor-pointer text-gray-700">
      {label}
    </div>
    {/if}
  </label>
</div>
