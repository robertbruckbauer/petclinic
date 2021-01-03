<script>
    import { onMount } from "svelte";
	import { toast } from '../components/Toast';
    import { loadOneValue } from "../utils/rest.js";

    export let id;

    let owner = {
        name: undefined
    };

    onMount(async () => {
        try {
            owner = await loadOneValue('/api/owner/' + id);
            console.log(['onMount', owner]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
    });
</script>

<h1>{owner.name}</h1>
