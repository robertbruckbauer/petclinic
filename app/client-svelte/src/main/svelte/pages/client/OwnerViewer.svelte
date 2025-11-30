<script>
  import { OwnerService } from "../../services/owner.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";

  export let id;

  const ownerService = new OwnerService();

  let owner = {
    name: undefined,
  };

  onMount(async () => {
    ownerService.loadOneOwner(id).subscribe({
      next: (json) => {
        owner = json;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  });
</script>

<h1>{owner.name}</h1>
