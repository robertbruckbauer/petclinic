<script>
	import { onMount } from 'svelte';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import Icon from '../components/Icon';
	import TextField from '../components/TextField';
	import VetEditor from './VetEditor.svelte';
	import VisitViewer from './VisitViewer.svelte';

	let allVet = [];
	let vetId = undefined;
	function onVetClicked(vet) {
		vetId = vet.id;		
		reloadAllVisit();
	}

	let vetEditorCreate = false;
	let vetEditorUpdate = false;
	$: vetEditorDisabled = vetEditorCreate || vetEditorUpdate;
	function vetEditorCreateClicked() {
		vetEditorCreate = true;
		visitViewerCreate = false;
	}    
	function vetEditorUpdateClicked(vet) {
		vetId = vet.id;
		vetEditorUpdate = true;
		visitViewerCreate = false;
	}
	
	let allVisit = [];

	let visitViewerCreate = false;
	function visitViewerCreateClicked(vet) {
		vetId = vet.id;
		visitViewerCreate = !visitViewerCreate;
	}

	let allSkillEnum = [];

    onMount(async () => {
        try {
			allSkillEnum = await loadAllValue('/api/enum/skill');
            allSkillEnum = allSkillEnum.map(e => ({
                value: e.value,
                text: e.name
            }))
            console.log(['onMount', allSkillEnum]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
        reloadAllVet()
	});

	let filterPrefix = '';
	$: allVetFiltered = filterVet(filterPrefix,allVet);
	function filterVet(prefix,allValue) {
		if (!filterPrefix) return allValue;
		return allValue.filter(e => {
			for (const s of e.name.split(" ")) {
				if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
					return true;
				}
			}
            return false;
		})
	}

	function reloadAllVet() {
		loadAllValue('/api/vet/search/findAllByOrderByNameAsc')
		.then(json => {
			console.log(['reloadAllVet', json]);
			allVet = json;
		})
		.catch(err => {
			console.log(['reloadAllVet', err]);
			toast.push(err.toString());
		});
	};

	function reloadAllVisit() {
		allVisit = [];
		if (!vetId) return;
		loadAllValue('/api/visit/search/findAllByVet?vetId=' + vetId)
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

<h1>Vet</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<TextField 
			bind:value={filterPrefix}
			disabled={vetEditorDisabled}
			label="Filter" 
			placeholder="Insert a criteria"/>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/3">
						<span class="text-gray-600">Name</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/3">
						<span class="text-gray-600">Special Skills</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon 
						on:click={() => vetEditorCreateClicked()}
							disabled={vetEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if vetEditorCreate}
				<tr>
					<td class="px-4" colspan="3">
						<VetEditor
							bind:visible={vetEditorCreate} 
							on:create={e => reloadAllVet()}/>
					<td>
				</tr>
				{/if}
				{#each allVetFiltered as vet}
				<tr on:click={e => onVetClicked(vet)}
					title={vet.id}
					class:ring={vetId === vet.id}>
					<td class="px-2 py-3 text-left">
						<div class="text-sm underline text-blue-600">
							<a href={'/vet/' + vet.id}>{vet.name}</a>
						</div>
					</td>
					<td class="px-2 py-3 text-left">
						{#each vet.allSkill as skill}
						<div class="flex flex-col">
							<span>{skill}</span>
						</div>
						{:else}
						<span>No skills</span>
						{/each}
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => visitViewerCreateClicked(vet)}
							title="Show all visits"
							disabled={vetEditorDisabled}
							name="list"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => vetEditorUpdateClicked(vet)}
							title="Edit vet details"
							disabled={vetEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if visitViewerCreate && vetId === vet.id}
				<tr>
					<td class="px-4" colspan="6">
						<VisitViewer
							bind:visible={visitViewerCreate} 
							{allVisit}/>
					<td>
				</tr>
				{/if}
				{#if vetEditorUpdate && vetId === vet.id}
				<tr>
					<td	class="px-4" colspan="3">
						<VetEditor
							bind:visible={vetEditorUpdate} 
							on:update={e => reloadAllVet()}
							on:remove={e => reloadAllVet()}
							{allSkillEnum}
							{vet}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="3">
						No vets
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>
