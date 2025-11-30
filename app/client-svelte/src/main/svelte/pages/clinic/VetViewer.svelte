<script>
  import { VetService } from "../../services/vet.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";

  export let id;

  const vetService = new VetService();

  let vet = {
    name: undefined,
  };

  onMount(async () => {
    vetService.loadOneVet(id).subscribe({
      next: (json) => {
        vet = json;
      },
      error: (err) => {
        toast.push(err.detail || err.toString());
      },
    });
  });
</script>

<h1>{vet.name}</h1>
