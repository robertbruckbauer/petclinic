<script>
    import { createEventDispatcher } from 'svelte';
	import { toast } from '../components/Toast';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import Button from '../components/Button';
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';
	import Toggle from '../components/Toggle';
    
    export let visible = false;
    export let aufgabe = undefined;
    export let projekt;

    let showUpdate;
    let showRemove;
    let newAufgabe = {
        termin: null,
        text: '',
        allStichwort: [],
        aktiv: true
    }

    $: if (aufgabe) onChangeAufgabe()
    function onChangeAufgabe() {
        showUpdate = true;
        showRemove = !aufgabe.aktiv;
        newAufgabe = {
            id: aufgabe.id,
            termin: aufgabe.termin,
            text: aufgabe.text,
            allStichwort: [...aufgabe.allStichwort],
            aktiv: aufgabe.aktiv
        };        
    }

    let allStichwortFromText = [];
    $: allStichwortFromText = [...allStichwortFromText, ...newAufgabe.text
            .replaceAll('\n', ' ')
            .replaceAll('\t', ' ')
            .replaceAll('.', '')
            .replaceAll(',', '')
            .toLowerCase()
            .split(' ')]
            .filter((e) => e.length)
            .filter((e,i,self) => self.indexOf(e) === i);

    $: disabled = !newAufgabe.termin || !newAufgabe.text;

    const dispatch = createEventDispatcher();
    function onCreateAufgabe() {
        newAufgabe.projekt = '/api/projekt/'  + projekt.id;
        createValue('/api/aufgabe', newAufgabe)
        .then(json => {
            console.log(['onCreateAufgabe', newAufgabe, json]);
            visible = false;
            dispatch('create', newAufgabe);
        })
        .catch(err => {
            console.log(['onCreateAufgabe', newAufgabe, err]);
            toast.push(err.toString());
        });;
    }
    function onUpdateAufgabe() {
        newAufgabe.projekt = '/api/projekt/'  + projekt.id;
        updatePatch('/api/aufgabe' + '/' + newAufgabe.id, newAufgabe)
        .then(json => {
            console.log(['onUpdateAufgabe', newAufgabe, json]);
            visible = false;
            dispatch('update', newAufgabe);
        })
        .catch(err => {
            console.log(['onUpdateAufgabe', newAufgabe, err]);
            toast.push(err.toString());
        });;
    }
    function onRemoveAufgabe() {
        const text = newAufgabe.text;
        const hint = text.length > 20 ? text.substring(0, 20) + '...' : text;
        if (!confirm("Aufgabe '" + hint + "' wirklich löschen?")) return;
        removeValue('/api/aufgabe' + '/' + newAufgabe.id)
        .then(() => {
            console.log(['onRemoveAufgabe', newAufgabe]);
            visible = false;
            dispatch('remove', newAufgabe);
        })
        .catch(err => {
            console.log(['onRemoveAufgabe', newAufgabe, err]);
            toast.push(err.toString());
        });;
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-48">
        <TextField 
            bind:value={newAufgabe.termin}
            type="date"
            label="Termin"		
            placeholder="Bitte den Termin eingeben"/>
    </div>
    <div class="w-full">
        <TextArea 
            bind:value={newAufgabe.text}
            label="Text"		
            placeholder="Bitte den Text eingeben"/>
    </div>
    <div class="w-full">
        <Toggle 
            bind:allValue={newAufgabe.allStichwort}
            allItem={allStichwortFromText} 
            label="Stichworte"            		
            placeholder="Bitte Stichworte wählen"/>
    </div>
</div>

<div class="py-4">
    {#if showUpdate}
    <Button on:click={() => onUpdateAufgabe()} {disabled}>
        Ok
    </Button>
    {:else}
    <Button on:click={() => onCreateAufgabe()} {disabled}>
        Ok
    </Button>
    {/if}
    {#if showRemove}
    <Button on:click={() => onRemoveAufgabe()}>
        Löschen
    </Button>
    {/if}
    <Button on:click={() => onCancel()}>
        Abbrechen
    </Button>
</div>

<details tabindex="-1">
    <summary>JSON</summary>
    <pre>{JSON.stringify(newAufgabe, null, 2)}</pre>
</details>
