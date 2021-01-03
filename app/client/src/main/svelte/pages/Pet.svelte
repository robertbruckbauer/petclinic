<script>
	import { onMount } from 'svelte';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
    import { storedOwnerId } from '../stores/owner.js';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
    import PetEditor from './PetEditor.svelte';

    let allPet = []; 
    let petOwnerId = null; 
	let petId = undefined;
	function onPetClicked(pet) {
		petId = pet.id;
	}

	let petEditorCreate = false;
	let petEditorUpdate = false;
	$: petEditorDisabled = petEditorCreate || petEditorUpdate;
	function petEditorCreateClicked() {
		petEditorCreate = true;
	}    
	function petEditorUpdateClicked(pet) {
		petId = pet.id;
		petEditorUpdate = true;
	}

    let allOwnerItem = [];

    let allSpeciesEnum = [];

    onMount(async () => {
        try {
			allOwnerItem = await loadAllValue('/api/owner/search/findAllItem');
            console.log(['onMount', allOwnerItem]);
			allSpeciesEnum = await loadAllValue('/api/enum/species');
            allSpeciesEnum = allSpeciesEnum.map(e => ({
                value: e.value,
                text: e.name
            }))
            console.log(['onMount', allSpeciesEnum]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
        petOwnerId = $storedOwnerId;
    });

    $: petOwnerId, reloadAllPet();
    function reloadAllPet() {
        if (!petOwnerId) return;
        loadAllValue('/api/pet/search/findAllByOwner?ownerId=' + petOwnerId)
        .then(json => {
            console.log(['reloadAllPet', json]);
            $storedOwnerId = petOwnerId;
            allPet = json;
        })
        .catch(err => {
            console.log(['reloadAllPet', err]);
            toast.push(err.toString());
        });
    }
</script>

<h1>Pet</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
    <div class="flex flex-row gap-1">
        <div class="w-full lg:w-96">
            <Select 
                bind:value={petOwnerId} 
                allItem={allOwnerItem} 
                disabled={petEditorDisabled}
                label="Owner"
                placeholder="Choose owner"/>
        </div>
    </div>
    <table class="table-fixed">
        <thead class="justify-between">
            <tr class="bg-gray-100">
                <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
                    <span class="text-gray-600">Species</span>
                </th>
                <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/2">
                    <span class="text-gray-600">Name</span>
                </th>
                <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
                    <span class="text-gray-600">Born</span>
                </th>
                <th class="px-2 py-3 border-b-2 border-gray-300 w-16">
                    <Icon 
                        on:click={() => petEditorCreateClicked()}
                        disabled={petEditorDisabled}
                        name="edit"
                        outlined/>
                </th>
            </tr>
        </thead>
        <tbody>
            {#if petEditorCreate}
            <tr>
                <td class="px-4" colspan="4">
                    <PetEditor
                        bind:visible={petEditorCreate} 
                        on:create={e => reloadAllPet()}
                        {allSpeciesEnum}
                        ownerId={petOwnerId}/>
                <td>
            </tr>
            {/if}
            {#each allPet as pet}
            <tr on:click={e => onPetClicked(pet)}
                title={pet.id}
                class:ring={petId === pet.id}>
                <td class="px-2 py-3 text-left">
                    <span>{pet.species}</span>
                </td>
                <td class="px-2 py-3 text-left">
                    <div class="text-sm underline text-blue-600">
                        <a href={'/pet/' + pet.id}>{pet.name}</a>
                    </div>
                </td>
                <td class="px-2 py-3 text-left">
                    <span>{pet.born}</span>
                </td>
                <td class="px-2 py-3">
                    <Icon 
                        on:click={() => petEditorUpdateClicked(pet)}
                        disabled={petEditorDisabled}
                        name="edit"
                        outlined/>
                </td>
            </tr>
            {#if petEditorUpdate && petId === pet.id}
            <tr>
                <td	class="px-4" colspan="4">
                    <PetEditor
                        bind:visible={petEditorUpdate} 
                        on:update={e => reloadAllPet()}
                        on:remove={e => reloadAllPet()}
                        {allSpeciesEnum}
                        ownerId={pet.ownerItem.value}
                        {pet}/>
                <td>
            </tr>
            {/if}
            {:else}
            <tr>
                <td class="px-2 py-3" colspan="4">
                    No pets
                </td>
            </tr>
            {/each}
        </tbody>
    </table>
</div>
