<script>
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';
	import { mapify } from '../utils/list.js';

    export let allVisit;

    let allVisitByDate = new Map();
    $: allVisitByDate = mapify(allVisit, visitKey);
    function visitKey(e) {
        return e.petItem.text + ' on ' + e.date;
    }
</script>

<div class="flex flex-col gap-1 mt-4">
    {#each [...allVisitByDate] as [title, l]}
    <details>
        <summary>{title}</summary>
        <div class="ml-4">
        {#each l as e}
        <TextField value={e.vetItem.text}
            label="Veterinarian" 
            disabled/>
        <TextArea value={e.text}
            label="Diagnosis" 
            disabled/>
        {/each}
        </div>
    </details>
    {:else}
    No visits
    {/each}
</div>
