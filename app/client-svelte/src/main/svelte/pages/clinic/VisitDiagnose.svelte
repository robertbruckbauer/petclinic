<script>
  import * as restApi from "../../services/rest.js";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";
  import Button from "../../components/Button";
  import Select from "../../components/Select";
  import TextArea from "../../components/TextArea";

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    allVetItem,
    visit,
    oncancel = undefined,
    onupdate = undefined,
  } = $props();

  let clicked = $state(false);
  let focusOn;
  let bottomDiv;
  onMount(async () => {
    console.log(["onMount", autofocus, autoscroll]);
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
  });

  let newVisitText = $derived(visit.text);
  let newVisitVetItem = $derived(visit.vetItem);
  const newVisit = $derived({
    id: visit.id,
    version: visit.version,
    text: newVisitText,
    vetItem: newVisitVetItem,
    vet: "/api/vet/" + newVisitVetItem.value,
  });

  function handleSubmit(_event) {
    _event.preventDefault();
    try {
      clicked = true;
      updateVisit();
    } finally {
      clicked = false;
    }
  }

  function handleCancel(_event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }

  function updateVisit() {
    restApi
      .updatePatch("/api/visit/" + newVisit.id, newVisit)
      .then((json) => {
        console.log(["createVisit", newVisit, json]);
        visible = false;
        onupdate?.(json);
      })
      .catch((err) => {
        console.log(["createVisit", newVisit, err]);
        toast.push(err.toString());
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
