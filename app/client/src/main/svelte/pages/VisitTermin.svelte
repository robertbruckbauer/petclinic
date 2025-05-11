<script>
  import { createEventDispatcher } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue } from "../utils/rest.js";
  import Button from "../components/Button";
  import TextField from "../components/TextField";

  export let visible = false;
  export let pet;

  let newVisit = {
    date: null,
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

  $: disabled = !newVisit.date || !newVisit.petItem || !newVisit.vetItem;

  const dispatch = createEventDispatcher();
  function onCreateVisit() {
    newVisit.pet = "/api/pet/" + pet.id;
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
  function onCancel() {
    visible = false;
  }
</script>

<div class="flex flex-col">
  <form class="w-full">
    <div class="w-full">
      <TextField
        bind:value={newVisit.date}
        type="date"
        label="Termin"
        placeholder="Bitte ein Datum eingeben"
      />
    </div>
  </form>
</div>

<div class="py-4">
  <Button on:click={() => onCreateVisit()} {disabled}>Ok</Button>
  <Button on:click={() => onCancel()}>Abbrechen</Button>
</div>

<details>
  <summary>JSON</summary>
  <pre>{JSON.stringify(newVisit, null, 2)}</pre>
</details>
