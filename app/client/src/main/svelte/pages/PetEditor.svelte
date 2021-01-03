<script>
    import { createEventDispatcher } from 'svelte';
	import { toast } from '../components/Toast';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import Button from '../components/Button';
	import Select from '../components/Select';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let pet = undefined;
    export let ownerId;
    export let allSpeciesEnum;

    let showUpdate;
    let showRemove;
    let newPet = {
        name: '',
        born: null,
        species: null
    }

    $: if (pet) onChangePet()
    function onChangePet() {
        showUpdate = true;
        showRemove = true;
        newPet = {
            id: pet.id,
            name: pet.name,
            born: pet.born,
            species: pet.species
        }
        console.log(['onChangePet', newPet]);
    }

    $: disabled = !newPet.name || !newPet.species;

    const dispatch = createEventDispatcher();
    function onCreatePet() {
        newPet.owner = '/api/owner/' + ownerId;
        createValue('/api/pet', newPet)
        .then(json => {
            console.log(['onCreatePet', newPet, json]);
            visible = false;
            dispatch('create', newPet);
        })
        .catch(err => {
            console.log(['onCreatePet', newPet, err]);
            toast.push(err.toString());
        });;
    }
    function onUpdatePet() {
        newPet.owner = '/api/owner/' + ownerId;
        updatePatch('/api/pet' + '/' + newPet.id, newPet)
        .then(json => {
            console.log(['onUpdatePet', newPet, json]);
            visible = false;
            dispatch('update', newPet);
        })
        .catch(err => {
            console.log(['onUpdatePet', newPet, err]);
            toast.push(err.toString());
        });;
    }
    function onRemovePet() {
        const text = newPet.name;
        const hint = text.length > 20 ? text.substring(0, 20) + '...' : text;
		if (!confirm("Really delete '" + hint + "'?")) return;
        removeValue('/api/pet' + '/' + newPet.id)
        .then(() => {
            console.log(['onRemovePet', newPet]);
            visible = false;
            dispatch('remove', newPet);
        })
        .catch(err => {
            console.log(['onRemovePet', newPet, err]);
            toast.push(err.toString());
        });;
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="flex flex-col lg:flex-row gap-1">
        <div class="w-full lg:w-1/4">
            <Select 
                bind:value={newPet.species}
                allItem={allSpeciesEnum} 
                label="Species"
                placeholder="Choose species"/>
        </div>
        <div class="w-full lg:w-2/4">
            <TextField 
                bind:value={newPet.name} 
                label="Name"		
                placeholder="Insert a name"/>
        </div>
        <div class="w-full lg:w-1/4">
            <TextField 
                bind:value={newPet.born}
                type="date"
                label="Born"		
                placeholder="Insert a date"/>
        </div>
    </div>
</div>

<div class="py-4">
    {#if showUpdate}
    <Button on:click={() => onUpdatePet()} {disabled}>
        Ok
    </Button>
    {:else}
    <Button on:click={() => onCreatePet()} {disabled}>
        Ok
    </Button>
    {/if}
    {#if showRemove}
    <Button on:click={() => onRemovePet()}>
        Löschen
    </Button>
    {/if}
    <Button on:click={() => onCancel()}>
        Abbrechen
    </Button>
</div>

<details>
    <summary>JSON</summary>
    <pre>{JSON.stringify(newPet, null, 2)}</pre>
</details>
