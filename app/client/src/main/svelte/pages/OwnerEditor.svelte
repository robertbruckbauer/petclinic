<script>
    import { createEventDispatcher } from 'svelte';
	import { toast } from '../components/Toast';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import Button from '../components/Button';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let owner = undefined;

    let showUpdate;
    let showRemove;
    let newOwner = {
        name: '',
        address: '',
        contact: ''
    }

    $: if (owner) onChangeOwner()
    function onChangeOwner() {
        showUpdate = true;
        showRemove = true;
        newOwner = {
            id: owner.id,
            name: owner.name,
            address: owner.address,
            contact: owner.contact
        }
        console.log(['onChangeOwner', newOwner]);
    }

    $: disabled = !newOwner.name || !newOwner.address || !newOwner.contact;

    const dispatch = createEventDispatcher();
    function onCreateOwner() {
        createValue('/api/owner', newOwner)
        .then(json => {
            console.log(['onCreateOwner', newOwner, json]);
            visible = false;
            dispatch('create', newOwner);
        })
        .catch(err => {
            console.log(['onCreateOwner', newOwner, err]);
            toast.push(err.toString());
        });;
    }
    function onUpdateOwner() {
        updatePatch('/api/owner' + '/' + newOwner.id, newOwner)
        .then(json => {
            console.log(['onUpdateOwner', newOwner, json]);
            visible = false;
            dispatch('update', newOwner);
        })
        .catch(err => {
            console.log(['onUpdateOwner', newOwner, err]);
            toast.push(err.toString());
        });;
    }
    function onRemoveOwner() {
        const text = newOwner.name;
        const hint = text.length > 20 ? text.substring(0, 20) + '...' : text;
		if (!confirm("Really delete '" + hint + "'?")) return;
        removeValue('/api/owner' + '/' + newOwner.id)
        .then(() => {
            console.log(['onRemoveOwner', newOwner]);
            visible = false;
            dispatch('remove', newOwner);
        })
        .catch(err => {
            console.log(['onRemoveOwner', newOwner, err]);
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
            bind:value={newOwner.name} 
            label="Name"		
            placeholder="Insert a name"/>
    </div>
    <div class="w-full">
        <TextField 
            bind:value={newOwner.address} 
            label="Address"		
            placeholder="Insert a text"/>
    </div>
    <div class="w-full">
        <TextField 
            bind:value={newOwner.contact} 
            label="Contact"		
            placeholder="Insert a text"/>
    </div>
</div>

<div class="py-4">
    {#if showUpdate}
    <Button on:click={() => onUpdateOwner()} {disabled}>
        Ok
    </Button>
    {:else}
    <Button on:click={() => onCreateOwner()} {disabled}>
        Ok
    </Button>
    {/if}
    {#if showRemove}
    <Button on:click={() => onRemoveOwner()}>
        Löschen
    </Button>
    {/if}
    <Button on:click={() => onCancel()}>
        Abbrechen
    </Button>
</div>

<details>
    <summary>JSON</summary>
    <pre>{JSON.stringify(newOwner, null, 2)}</pre>
</details>
