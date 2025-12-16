<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../controls/Toast";
  import { VisitService } from "../../services/visit.service";
  import type { VetItem } from "../../types/vet.type";
  import type { Visit } from "../../types/visit.type";

  const visitService = new VisitService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    allVetItem: VetItem[];
    visit: Visit;
    oncancel?: undefined | (() => void);
    onupdate?: undefined | ((visit: Visit) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    allVetItem,
    visit,
    oncancel = undefined,
    onupdate = undefined,
  }: Props = $props();

  let clicked = $state(false);
  let focusOn: any;
  let bottomDiv: HTMLElement;
  onMount(async () => {
    console.log(["onMount", autofocus, autoscroll]);
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
  });

  let newVisitText = $derived(visit.text);
  let newVisitVetId = $derived(visit.vetItem?.value);
  const newVisit = $derived({
    ...visit,
    text: newVisitText,
    vetItem: undefined, // vetItem is invalid
    vet: newVisitVetId ? "/api/vet/" + newVisitVetId : undefined,
  });

  function onSubmitClicked(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      updateVisit();
    } finally {
      clicked = false;
    }
  }

  function onCancelClicked(_event: Event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function updateVisit() {
    visitService.mutateVisit(newVisit.id!, newVisit).subscribe({
      next: (json) => {
        visible = false;
        onupdate?.(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }
</script>

<form onsubmit={onSubmitClicked}>
  <div class="flex flex-col gap-2 pt-4">
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Diagnose</legend>
      <textarea
        bind:this={focusOn}
        bind:value={newVisitText}
        aria-label="Diagnose"
        class="textarea w-full"
        placeholder="Enter a diagnose"
      ></textarea>
    </fieldset>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Vet</legend>
      <select bind:value={newVisitVetId} aria-label="Vet" class="select w-full">
        <option value="" disabled selected>Choose a vet</option>
        {#each allVetItem as vetItem}
          <option value={vetItem.value}>{vetItem.text}</option>
        {/each}
      </select>
    </fieldset>
  </div>
  <div class="join py-4">
    <button type="submit" class="btn join-item">Ok</button>
    <button type="button" class="btn join-item" onclick={onCancelClicked}>
      Cancel
    </button>
  </div>
</form>

<div class="h-0" bind:this={bottomDiv}>&nbsp;</div>

<details>
  <summary>JSON</summary>
  <pre>{JSON.stringify(newVisit, null, 2)}</pre>
</details>
