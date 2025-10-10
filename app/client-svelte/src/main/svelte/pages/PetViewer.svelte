<script>
  import * as restApi from "../services/rest.js";
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";

  export let id;

  let pet = {
    name: undefined,
  };

  onMount(async () => {
    try {
      pet = await restApi.loadOneValue("/api/pet/" + id);
      console.log(["onMount", pet]);
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    }
  });
</script>

<h1>{pet.name}</h1>
