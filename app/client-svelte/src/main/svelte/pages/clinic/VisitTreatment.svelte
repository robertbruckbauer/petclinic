<script>
  import { VisitService } from "../../services/visit.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import TextField from "../../components/TextField";

  const visitService = new VisitService();

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    pet,
    oncancel = undefined,
    oncreate = undefined,
  } = $props();

  let clicked = $state(false);
  let focusOn;
  let bottomDiv;
  onMount(async () => {
    console.log(["onMount", autofocus, autoscroll]);
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
  });

  let newVisitDate = $state();
  const newVisit = $derived({
    pet: "/api/pet/" + pet.id,
    date: newVisitDate,
  });

  function handleSubmit(_event) {
    _event.preventDefault();
    try {
      clicked = true;
      createVisit();
    } finally {
      clicked = false;
    }
  }

  function handleCancel(_event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function createVisit() {
    visitService.createVisit(newVisit).subscribe({
      next: (json) => {
        console.log(["createVisit", newVisit, json]);
        visible = false;
        oncreate?.(json);
      },
      error: (err) => {
        console.log(["createVisit", newVisit, err]);
        toast.push(err.detail || err.toString());
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
