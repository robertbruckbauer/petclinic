<script>
  import * as restApi from "../../services/rest.js";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";

  export let id;

  let owner = {
    name: undefined,
  };

  onMount(async () => {
    try {
      owner = await restApi.loadOneValue("/api/owner/" + id);
      console.log(["onMount", owner]);
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    }
  });
</script>

<h1>{owner.name}</h1>
