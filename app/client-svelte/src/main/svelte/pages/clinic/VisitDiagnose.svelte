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
    oncreate?: undefined | ((visit: Visit) => void);
    onupdate?: undefined | ((visit: Visit) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    allVetItem,
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

  function parseDuration(iso: string): { h: number; m: number; s: number } {
    const match = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?$/.exec(iso ?? "");
    return {
      h: match ? Number(match[1] ?? 0) : 0,
      m: match ? Number(match[2] ?? 0) : 0,
      s: match ? Number(match[3] ?? 0) : 0,
    };
  }

  function composeDuration(
    h: number,
    m: number,
    s: number
  ): string | undefined {
    if (!h && !m && !s) return undefined;
    return "PT" + (h ? h + "H" : "") + (m ? m + "M" : "") + (s ? s + "S" : "");
  }

  let newVisitText = $derived(visit.text);
  let newVisitTime = $state("");
  let newVisitDurationH = $state(0);
  let newVisitDurationM = $state(0);
  let newVisitDurationS = $state(0);
  $effect(() => {
    newVisitTime = visit.time ?? "";
    const d = parseDuration(visit.duration ?? "");
    newVisitDurationH = d.h;
    newVisitDurationM = d.m;
    newVisitDurationS = d.s;
  });
  let newVisitPetId = $derived(visit.petItem?.value);
  let newVisitVetId = $derived(visit.vetItem?.value);
  const newVisit = $derived({
    ...visit,
    text: newVisitText,
    time: newVisitTime || undefined,
    duration: composeDuration(
      newVisitDurationH,
      newVisitDurationM,
      newVisitDurationS
    ),
    petItem: undefined, // petItem is invalid
    pet: newVisitPetId ? "/api/pet/" + newVisitPetId : undefined,
    vetItem: undefined, // vetItem is invalid
    vet: newVisitVetId ? "/api/vet/" + newVisitVetId : undefined,
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
    <div class="flex flex-col sm:flex-row gap-2">
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Time</legend>
        <input
          bind:value={newVisitTime}
          aria-label="Time"
          type="time"
          class="input w-full"
        />
      </fieldset>
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Duration</legend>
        <div class="flex gap-1">
          <input
            bind:value={newVisitDurationH}
            aria-label="Hours"
            type="number"
            min="0"
            class="input w-full"
            placeholder="h"
          />
          <input
            bind:value={newVisitDurationM}
            aria-label="Minutes"
            type="number"
            min="0"
            max="59"
            class="input w-full"
            placeholder="m"
          />
          <input
            bind:value={newVisitDurationS}
            aria-label="Seconds"
            type="number"
            min="0"
            max="59"
            class="input w-full"
            placeholder="s"
          />
        </div>
      </fieldset>
    </div>
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

{#if import.meta.env.DEV}
  <details>
    <summary>JSON</summary>
    <pre>{JSON.stringify(newVisit, null, 2)}</pre>
  </details>
{/if}
