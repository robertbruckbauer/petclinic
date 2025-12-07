<script lang="ts">
  import { onMount } from "svelte";
  import { VisitService } from "../../services/visit.service";
  import type { VetItem } from "../../types/vet.type";
  import type { Visit } from "../../types/visit.type";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import Select from "../../components/Select";
  import TextArea from "../../components/TextArea";

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
  let newVisitVetItem = $derived(visit.vetItem);
  const newVisit = $derived({
    ...visit,
    text: newVisitText,
    vetItem: newVisitVetItem,
    vet: newVisitVetItem ? "/api/vet/" + newVisitVetItem.value : undefined,
  });

  function handleSubmit(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      updateVisit();
    } finally {
      clicked = false;
    }
  }

  function handleCancel(_event: Event) {
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

<div class="flex flex-col">
  <form onsubmit={handleSubmit}>
    <div class="w-full">
      <TextArea
        bind:this={focusOn}
        bind:value={newVisitText}
        required
        label="Diagnosis"
        placeholder="Insert diagnosis"
      />
    </div>
    <div class="full">
      <Select
        bind:value={newVisitVetItem}
        allItem={allVetItem}
        required
        label="Vet"
        placeholder="Choose vet"
      />
    </div>
    <div class="py-4 flex flex-row gap-1 items-baseline">
      <div class="flex-initial">
        <Button type="submit">Ok</Button>
      </div>
      <div class="flex-initial">
        <Button type="button" onclick={handleCancel}>Abbrechen</Button>
      </div>
    </div>
  </form>
</div>

<div class="h-0" bind:this={bottomDiv}>&nbsp;</div>

<details>
  <summary>JSON</summary>
  <pre>{JSON.stringify(newVisit, null, 2)}</pre>
</details>
