<script lang="ts">
  let {
    label = undefined,
    title = undefined,
    value,
    ...elementProps
  } = $props();

  let valueInternal = $state(value);
  $effect(() => {
    valueInternal = value ? value : "";
  });
</script>

<div class="mt-1 relative">
  {#if label}
    <span class="px-4 pt-2 text-xs absolute left-0 top-0">
      {label}
    </span>
  {/if}
  <input
    type="text"
    aria-label={label}
    {...elementProps}
    {title}
    value={valueInternal}
    disabled
    class="w-full px-4 text-black bg-gray-100 border-0"
    class:pt-6={label}
  />
  {#if label}
    <hr class="w-full bg-gray-600 absolute left-0 bottom-0" />
  {/if}
</div>
