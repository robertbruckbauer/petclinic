<script>
	import { onMount } from 'svelte';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { mapify } from '../utils/list.js';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
	import VisitEditor from './VisitEditor.svelte';

	let allVisit = [];
	let visitId = undefined;
	function onVisitClicked(visit) {
		visitId = visit.id;	
	}

	let visitEditorCreate = false;
	let visitEditorUpdate = false;
	$: visitEditorDisabled = visitEditorCreate || visitEditorUpdate;
	function visitEditorCreateClicked() {
		visitEditorCreate = true;
	}    
	function visitEditorUpdateClicked(visit) {
		visitId = visit.id;
		visitEditorUpdate = true;
	}

	let allPetItem = [];

	let allVetItem = [];

	let allSpeciesEnum = [];

    onMount(async () => {
        try {
            allPetItem = await loadAllValue('/api/pet/search/findAllItem');
            console.log(['onMount', allPetItem]);
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
        reloadAllVisit()
	});

	let filterPrefix = null;
	$: allVisitFiltered = filterVisit(filterPrefix, allVisit);
	function filterVisit(prefix,allValue) {
		if (!filterPrefix) return allValue;
		return allValue.filter(e => {
			for (const s of e.petItem.text.split(" ")) {
				if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
					return true;
				}
			}
            return false;
		})
	}
	
    $: allVisitByDate = mapify(allVisitFiltered, visitKey);
    function visitKey(e) {
        return e.date;
    }

	function reloadAllVisit() {
		loadAllValue('/api/visit/search/findAllByOrderByDateDesc')
		.then(json => {
			console.log(['reloadAllVisit', json]);
			allVisit = json;
		})
		.catch(err => {
			console.log(['reloadAllVisit', err]);
			toast.push(err.toString());
		});
	};
</script>

<h1>Visit</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<Select 
			bind:value={filterPrefix} 
			allItem={allSpeciesEnum}
			disabled={visitEditorDisabled}
			nullable="true"
			label="Filter"
			placeholder="Choose species"/>
	</div>
	<div class="flex-grow">
		{#each [...allVisitByDate] as [date, allVisitOfDate], i}
		<h4>{date} <small>({allVisitOfDate.length})</small></h4>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/6">
						<span class="text-gray-600">Owner</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/6">
						<span class="text-gray-600">Pet</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/6">
						<span class="text-gray-600">Vet</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon 
							on:click={() => visitEditorCreateClicked()}
							disabled={visitEditorDisabled}
							name="edit"
							outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if visitEditorCreate}
				<tr>
					<td class="px-4" colspan="3">
						<VisitEditor
							bind:visible={visitEditorCreate} 
							on:create={e => reloadAllVisit()}
							{date}
							{allPetItem}
							{allVetItem}/>
					<td>
				</tr>
				{/if}
				{#each allVisitOfDate as visit}
				<tr on:click={e => onVisitClicked(visit)}
					title={visit.id}
					class:ring={visitId === visit.id}>
					<td class="px-2 py-3 text-left">
						{visit.ownerItem.text}
					</td>
					<td class="px-2 py-3 text-left">
						{visit.petItem.text}
					</td>
					<td class="px-2 py-3 text-left">
						{visit.vetItem.text}
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => visitEditorUpdateClicked(visit)}
							title="Edit visit details"
							disabled={visitEditorDisabled}
							name="edit"
							outlined/>
					</td>
				</tr>
				{#if visitEditorUpdate && visitId === visit.id}
				<tr>
					<td	class="px-4" colspan="4">
						<VisitEditor
							bind:visible={visitEditorUpdate} 
							on:update={e => reloadAllVisit()}
							on:remove={e => reloadAllVisit()}
							{date}
							{allPetItem}
							{allVetItem}
							{visit}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="4">
						No visits
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
		{:else}
		<tr>
			<td class="px-2 py-3" colspan="6">
				No visits
			</td>
		</tr>
		{/each}
	</div>
</div>
