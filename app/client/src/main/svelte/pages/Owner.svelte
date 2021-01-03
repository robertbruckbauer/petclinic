<script>
	import { onMount } from 'svelte';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import Icon from '../components/Icon';
	import TextField from '../components/TextField';
	import OwnerEditor from './OwnerEditor.svelte';
	import PetEditor from './PetEditor.svelte';
	import VisitEditor from './VisitEditor.svelte';
	import VisitViewer from './VisitViewer.svelte';

	let allOwner = [];
	let ownerId = undefined;
	function onOwnerClicked(owner) {
		ownerId = owner.id;
		reloadAllVisit();
	}

	let ownerEditorCreate = false;
	let ownerEditorUpdate = false;
	$: ownerEditorDisabled = ownerEditorCreate || ownerEditorUpdate || petEditorCreate || visitEditorCreate;
	function ownerEditorCreateClicked() {
		ownerEditorCreate = true;
		visitViewerCreate = false;
	}    
	function ownerEditorUpdateClicked(owner) {
		ownerId = owner.id;
		ownerEditorUpdate = true;
		visitViewerCreate = false;
	}

	let petEditorCreate = false;
	function petEditorCreateClicked(owner) {
		ownerId = owner.id;
		petEditorCreate = true;
		visitViewerCreate = false;
	}
	
	let allVisit = [];

	let visitEditorCreate = false;
	function visitEditorCreateClicked(owner) {
		ownerId = owner.id;
		visitEditorCreate = true;
		visitViewerCreate = false;
	}

	let visitViewerCreate = false;
	function visitViewerCreateClicked(owner) {
		ownerId = owner.id;
		visitViewerCreate = !visitViewerCreate;
	}

	let allVetItem = [];

	let allSpeciesEnum = [];

    onMount(async () => {
        try {
            allVetItem = await loadAllValue('/api/vet/search/findAllItem');
            console.log(['onMount', allVetItem]);
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
		reloadAllOwner();
	});

	let filterPrefix = '';
	$: allOwnerFiltered = filterOwner(filterPrefix, allOwner);
	function filterOwner(prefix,allValue) {
		if (!filterPrefix) return allValue;
		return allValue.filter(e => {
			for (const s of e.name.split(" ")) {
				if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
					return true;
				}
			}
            return false;
		})
	};

	function reloadAllOwner() {
		loadAllValue('/api/owner/search/findAllByOrderByNameAsc')
		.then(json => {
			console.log(['reloadAllOwner', json]);
			allOwner = json;
		})
		.catch(err => {
			console.log(['reloadAllOwner', err]);
			toast.push(err.toString());
		});
	};

	function reloadAllVisit() {
		allVisit = [];
		if (!ownerId) return;
		loadAllValue('/api/visit/search/findAllByOwner?ownerId=' + ownerId)
		.then(json => {
			console.log(['reloadAllVisit', json]);
			allVisit = json;
		})
		.catch(err => {
			console.log(['reloadAllVisit', err]);
			toast.push(err.toString());
		});
	}
</script>

<h1>Owner</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<TextField 
			bind:value={filterPrefix}
			disabled={ownerEditorDisabled}
			label="Filter" 
			placeholder="Insert a criteria"/>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/3">
						<span class="text-gray-600">Name</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-full">
						<span class="text-gray-600">Pets</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon 
							on:click={() => ownerEditorCreateClicked()}
							title="Add a new owner"
							disabled={ownerEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if ownerEditorCreate}
				<tr>
					<td class="px-4" colspan="6">
						<OwnerEditor
							bind:visible={ownerEditorCreate} 
							on:create={e => reloadAllOwner()}/>
					<td>
				</tr>
				{/if}
				{#each allOwnerFiltered as owner}
				<tr on:click={e => onOwnerClicked(owner)}
					title={owner.id}
					class:ring={ownerId === owner.id}>
					<td class="px-2 py-3 text-left">
						<div class="text-sm underline text-blue-600">
							<a href={'/owner/' + owner.id}>{owner.name}</a>
						</div>
					</td>
					<td class="px-2 py-3 text-left">						
						<div class="flex flex-col">
							{#each owner.allPetItem as petItem}
							<div class="text-sm underline text-blue-600">
								<a href={'/pet/' + petItem.value}>{petItem.text}</a>
							</div>
							{:else}
							<span>No pets</span>
							{/each}
						</div>
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => visitViewerCreateClicked(owner)}
							title="Show all visits"
							disabled={ownerEditorDisabled}
							name="list"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => visitEditorCreateClicked(owner)}
							title="Add a new visit"
							disabled={ownerEditorDisabled}
							name="event"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => petEditorCreateClicked(owner)}
							title="Add a new pet"
							disabled={ownerEditorDisabled}
							name="pets"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => ownerEditorUpdateClicked(owner)}
							title="Edit owner details"
							disabled={ownerEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if visitViewerCreate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="6">
						<VisitViewer
							bind:visible={visitViewerCreate} 
							{allVisit}/>
					<td>
				</tr>
				{/if}
				{#if visitEditorCreate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="6">
						<VisitEditor
							bind:visible={visitEditorCreate} 
							on:create={e => reloadAllOwner()}
							allPetItem={owner.allPetItem}
							allVetItem={allVetItem}/>
					<td>
				</tr>
				{/if}
				{#if petEditorCreate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="6">
						<PetEditor
							bind:visible={petEditorCreate} 
							on:create={e => reloadAllOwner()}
							{allSpeciesEnum}
							ownerId={owner.id}/>
					<td>
				</tr>
				{/if}
				{#if ownerEditorUpdate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="6">
						<OwnerEditor
							bind:visible={ownerEditorUpdate} 
							on:update={e => reloadAllOwner()}
							on:remove={e => reloadAllOwner()}
							{owner}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="6">
						No owners
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>
