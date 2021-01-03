<script>
  export let disabled;
  export let group;
  export let label;
  export let value;
  export let title;

  $: valueProcessed = process(value);
  $: groupProcessed = group.map(process);
  function process(e) {
    if (typeof e !== "object") {
      return e;
    } else {
      return e ? JSON.stringify(e) : null;
    }
  }

	function onChange({ target }) {
		if (target.checked) {
			groupProcessed = [...groupProcessed, valueProcessed];
			group = [...group, value]
		} else {
			groupProcessed = groupProcessed.filter((e) => e !== valueProcessed);
			group = group.filter((e) => e !== value);
		}
	}
</script>

<div class="relative w-auto h-auto">
  <label {title} class="inline-flex items-center p-2 cursor-pointer">
    <input
      {title}
      class="disabled:opacity-50 border-2 border-primary-500 text-primary-500"
      type=checkbox
      {disabled} 
      checked={groupProcessed.includes(valueProcessed)}
      on:change={onChange}
      on:change/>
    {#if label}
    <div class="pl-2 cursor-pointer text-gray-700">
      {label}
    </div>
    {/if}
  </label>
</div>
