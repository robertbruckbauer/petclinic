<script>
  import * as restApi from "../utils/rest.js";
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";

  export let id;

  let vet = {
    name: undefined,
  };

  onMount(async () => {
    try {
      vet = await restApi.loadOneValue("/api/vet/" + id);
      console.log(["onMount", vet]);
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    }
  });
</script>

<h1>{vet.name}</h1>
