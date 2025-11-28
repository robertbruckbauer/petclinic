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
        console.log(["onMount", pet]);
      },
      error: (err) => {
        console.log(["onMount", err]);
        toast.push(err.detail || err.toString());
      },
    });
  });
</script>

<h1>{pet.name}</h1>
