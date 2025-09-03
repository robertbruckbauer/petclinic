<script>
  import { createEventDispatcher } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue } from "../utils/rest.js";
  import { updatePatch } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import Button from "../components/Button";
  import Select from "../components/Select";
  import TextArea from "../components/TextArea";

  export let visible = false;
  export let allVetItem;
  export let visit = undefined;
  export let date = null;

  let showUpdate;
  let showRemove;
  let newVisit = {
    date: date,
    text: "",
    petItem: {
      value: null,
      text: "",
    },
    vetItem: {
      value: null,
      text: "",
    },
  };

  $: if (visit) onChangeVisit();
  function onChangeVisit() {
    showUpdate = true;
    showRemove = true;
    newVisit = {
      id: visit.id,
      date: visit.date,
      text: visit.text,
      petItem: {
        value: visit.petItem.value,
        text: visit.petItem.text,
      },
      vetItem: {
        value: visit.vetItem.value,
        text: visit.vetItem.text,
      },
    };
    console.log(["onChangeVisit", newVisit]);
  }

  $: disabled = !newVisit.text || !newVisit.vetItem.value;

  const dispatch = createEventDispatcher();
  function onCreateVisit() {
    newVisit.pet = "/api/pet/" + newVisit.petItem.value;
    newVisit.vet = "/api/vet/" + newVisit.vetItem.value;
    createValue("/api/visit", newVisit)
      .then((json) => {
        console.log(["onCreateVisit", newVisit, json]);
        visible = false;
        dispatch("create", newVisit);
      })
      .catch((err) => {
        console.log(["onCreateVisit", newVisit, err]);
        toast.push(err.toString());
      });
  }
  function onUpdateVisit() {
    newVisit.pet = "/api/pet/" + newVisit.petItem.value;
    newVisit.vet = "/api/vet/" + newVisit.vetItem.value;
    updatePatch("/api/visit" + "/" + newVisit.id, newVisit)
      .then((json) => {
        console.log(["onUpdateVisit", newVisit, json]);
        visible = false;
        dispatch("update", newVisit);
      })
      .catch((err) => {
        console.log(["onUpdateVisit", newVisit, err]);
        toast.push(err.toString());
      });
  }
  function onRemoveVisit() {
    const text = newVisit.petItem.text;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    newVisit.vet = "/api/vet/" + newVisit.vetItem.value;
    if (!confirm("Really delete visit for '" + hint + "'?")) return;
    removeValue("/api/visit" + "/" + newVisit.id)
      .then(() => {
        console.log(["onRemoveVisit", newVisit]);
        visible = false;
        dispatch("remove", newVisit);
      })
      .catch((err) => {
        console.log(["onRemoveVisit", newVisit, err]);
        toast.push(err.toString());
      });
  }
  function onCancel() {
    visible = false;
  }
</script>

<div class="flex flex-col">
  <form class="w-full">
    <div class="w-full">
      <TextArea
        bind:value={newVisit.text}
        required
        label="Diagnosis"
        placeholder="Insert diagnosis"
      />
    </div>
    <div class="full">
      <Select
        bind:value={newVisit.vetItem}
        allItem={allVetItem}
        label="Vet"
        placeholder="Choose vet"
      />
    </div>
  </form>
</div>

<div class="py-4">
  {#if showUpdate}
    <Button onclick={() => onUpdateVisit()} {disabled}>Ok</Button>
  {:else}
    <Button onclick={() => onCreateVisit()} {disabled}>Ok</Button>
  {/if}
  {#if showRemove}
    <Button onclick={() => onRemoveVisit()}>Löschen</Button>
  {/if}
  <Button onclick={() => onCancel()}>Abbrechen</Button>
</div>

<details>
  <summary>JSON</summary>
  <pre>{JSON.stringify(newVisit, null, 2)}</pre>
</details>
