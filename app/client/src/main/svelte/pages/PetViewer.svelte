<script>
    import { onMount } from "svelte";
	import { toast } from '../components/Toast';
    import { loadOneValue } from "../utils/rest.js";

    export let id;

    let pet = {
        name: undefined
    };

    onMount(async () => {
        try {
            pet = await loadOneValue('/api/pet/' + id);
            console.log(['onMount', pet]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
    });
</script>

<h1>{pet.name}</h1>
