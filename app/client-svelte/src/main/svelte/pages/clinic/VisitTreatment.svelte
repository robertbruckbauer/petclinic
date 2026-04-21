<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../controls/Toast";
  import { VisitService } from "../../services/visit.service";
  import type { Visit } from "../../types/visit.type";

  const visitService = new VisitService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    visit: Visit;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((visit: Visit) => void);
    onupdate?: undefined | ((visit: Visit) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    visit,
    oncancel = undefined,
    oncreate = undefined,
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

  let newVisitDate = $state("");
  let newVisitTime = $state("");
  $effect(() => {
    newVisitDate = visit.date ?? "";
    newVisitTime = visit.time ?? "";
  });
  let newVisitPetId = $derived(visit.petItem?.value);
  const newVisit: Visit = $derived({
    ...visit,
    petItem: undefined, // petItem is invalid
    pet: newVisitPetId ? "/api/pet/" + newVisitPetId : undefined,
    date: newVisitDate,
    time: newVisitTime || undefined,
  });

  function onSubmitClicked(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      if (visit.id) {
        visitService.mutateVisit(newVisit.id!, newVisit).subscribe({
          next: (json) => {
            visible = false;
            onupdate?.(json);
          },
          error: (err) => {
            toast.push(err);
          },
        });
      } else {
        visitService.createVisit(newVisit).subscribe({
          next: (json) => {
            visible = false;
            oncreate?.(json);
          },
          error: (err) => {
            toast.push(err);
          },
        });
      }
    } finally {
      clicked = false;
    }
  }

  function onCancelClicked(_event: Event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }
</script>

<form onsubmit={onSubmitClicked}>
  <div class="flex flex-col sm:flex-row gap-2 pt-4">
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Treatment</legend>
      <input
        bind:this={focusOn}
        bind:value={newVisitDate}
        aria-label="Treatment"
        type="date"
        class="input w-full"
        placeholder="Choose a date"
      />
    </fieldset>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Time</legend>
      <input
        bind:value={newVisitTime}
        aria-label="Time"
        type="time"
        class="input w-full"
      />
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

{#if import.meta.env.DEV}
  <details>
    <summary>JSON</summary>
    <pre>{JSON.stringify(newVisit, null, 2)}</pre>
  </details>
{/if}
