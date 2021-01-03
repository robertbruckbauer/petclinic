<script>
    import { onMount } from "svelte";
	import { toast } from '../components/Toast';
    import { loadOneValue } from "../utils/rest.js";

    export let id;

    let vet = {
        name: undefined
    };

    onMount(async () => {
        try {
            vet = await loadOneValue('/api/vet/' + id);
            console.log(['onMount', vet]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
    });
</script>

<h1>{vet.name}</h1>
