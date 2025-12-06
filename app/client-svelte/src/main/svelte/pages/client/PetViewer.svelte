<script>
  import { PetService } from "../../services/pet.service";
  import { onMount } from "svelte";
  import { toast } from "../../components/Toast";

  export let id;

  const petService = new PetService();

  let pet = {
    name: undefined,
  };

  onMount(async () => {
    petService.loadOnePet(id).subscribe({
      next: (json) => {
        pet = json;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  });
</script>

<h1>{pet.name}</h1>
