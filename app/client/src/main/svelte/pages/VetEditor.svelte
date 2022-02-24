<script>
    import { createEventDispatcher } from 'svelte';
	import { toast } from '../components/Toast';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import Button from '../components/Button';
	import TextField from '../components/TextField';
	import Toggle from '../components/Toggle';
    
    export let visible = false;
    export let vet = undefined;
    export let allSkillEnum;

    let showUpdate;
    let showRemove;
    let newVet = {
        name: '',
        allSkill: []
    }

    $: if (vet) onChangeVet()
    function onChangeVet() {
        showUpdate = true;
        showRemove = true;
        newVet = {
            id: vet.id,
            name: vet.name,
            allSkill: [...vet.allSkill]
        }
        console.log(['onChangeVet', newVet]);
    }

    $: disabled = !newVet.name;

    const dispatch = createEventDispatcher();
    function onCreateVet() {
        createValue('/api/vet', newVet)
        .then(json => {
            console.log(['onCreateVet', newVet, json]);
            visible = false;
            dispatch('create', newVet);
        })
        .catch(err => {
            console.log(['onCreateVet', newVet, err]);
            toast.push(err.toString());
        });;
    }
    function onUpdateVet() {
        updatePatch('/api/vet' + '/' + newVet.id, newVet)
        .then(json => {
            console.log(['onUpdateVet', newVet, json]);
            visible = false;
            dispatch('update', newVet);
        })
        .catch(err => {
            console.log(['onUpdateVet', newVet, err]);
            toast.push(err.toString());
        });;
    }
    function onRemoveVet() {
        const text = newVet.name;
        const hint = text.length > 20 ? text.substring(0, 20) + '...' : text;
		if (!confirm("Really delete '" + hint + "'?")) return;
        removeValue('/api/vet' + '/' + newVet.id)
        .then(() => {
            console.log(['onRemoveVet', newVet]);
            visible = false;
            dispatch('remove', newVet);
        })
        .catch(err => {
            console.log(['onRemoveVet', newVet, err]);
            toast.push(err.toString());
        });;
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField 
            bind:value={newVet.name} 
            label="Name"		
            placeholder="Insert a name"/>
    </div>
    <div class="w-full">
        <Toggle 
            bind:allValue={newVet.allSkill}
            allItem={allSkillEnum} 
            label="Skills"            		
            placeholder="Insert skills"/>
    </div>
</div>

<div class="py-4">
    {#if showUpdate}
    <Button on:click={() => onUpdateVet()} {disabled}>
        Ok
    </Button>
    {:else}
    <Button on:click={() => onCreateVet()} {disabled}>
        Ok
    </Button>
    {/if}
    {#if showRemove}
    <Button on:click={() => onRemoveVet()}>
        Löschen
    </Button>
    {/if}
    <Button on:click={() => onCancel()}>
        Abbrechen
    </Button>
</div>

<details>
    <summary>JSON</summary>
    <pre>{JSON.stringify(newVet, null, 2)}</pre>
</details>
