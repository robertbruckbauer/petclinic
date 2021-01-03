<script>
  import { scale } from "svelte/transition";
  import { quadIn } from "svelte/easing";
  export let value;
  let dialog;
  const onOpen = dialog => {
    const nodes = dialog.querySelectorAll('*');
		const tabbable = Array.from(nodes).filter(n => n.tabIndex >= 0);
		tabbable[0].focus();
  };
	const onKeydown = e => {
    if (!dialog) {
      return;
    }
		if (e.key === 'Escape') {
			value = false;
			return;
		}
		if (e.key === 'Tab') {
			const nodes = dialog.querySelectorAll('*');
			const tabbable = Array.from(nodes).filter(node => node.tabIndex >= 0);
			let index = tabbable.indexOf(document.activeElement);
			if (index === -1 && e.shiftKey) index = 0;
			index += tabbable.length + (e.shiftKey ? -1 : 1);
			index %= tabbable.length;
			tabbable[index].focus();
			e.preventDefault();
			return;
		}
	};
</script>

<svelte:window on:keydown={onKeydown}/>

{#if value}
<div 
  class="fixed w-full h-full top-0 left-0 z-20"
  role="dialog"
  aria-modal="true"
>
  <div
    class="bg-black fixed top-0 left-0 w-full h-full"
    style="opacity: 0.5"
    disabled
  />
  <div 
    class="absolute flex h-full w-full items-center justify-center"
  >
    <dialog
      bind:this={dialog}
      use:onOpen
      open
      class="items-center rounded bg-white p-4 shadow"
      in:scale={{ 
        duration: 150, 
        easing: 
        quadIn, 
        delay: 150 
      }}
    >
      <div class="text-lg font-bold pb-4">
        <slot name="title" />
      </div>
      <slot />
      <div class="flex w-full justify-end pt-4">
        <slot name="actions" />
      </div>
    </dialog>
  </div>
</div>
{/if}
