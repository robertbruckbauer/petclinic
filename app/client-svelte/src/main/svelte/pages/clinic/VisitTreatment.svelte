<script lang="ts">
  import { onMount } from "svelte";
  import { VisitService } from "../../services/visit.service";
  import type { Pet } from "../../types/pet.type";
  import type { Visit } from "../../types/visit.type";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import TextField from "../../components/TextField";

  const visitService = new VisitService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    pet: Pet;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((visit: Visit) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    pet,
    oncancel = undefined,
    oncreate = undefined,
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
  const newVisit: Visit = $derived({
    version: 0,
    pet: "/api/pet/" + pet.id,
    date: newVisitDate,
  });

  function handleSubmit(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      createVisit();
    } finally {
      clicked = false;
    }
  }

  function handleCancel(_event: Event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function createVisit() {
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
</script>

<div class="flex flex-col">
  <form onsubmit={handleSubmit}>
    <div class="w-full">
      <TextField
        bind:this={focusOn}
        bind:value={newVisitDate}
        type="date"
        required
        label="Treatment"
        placeholder="Choose a date"
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
