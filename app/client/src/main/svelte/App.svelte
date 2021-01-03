<script>
	import './index.css';
    import Menu from "./components/Menu";
    import Toast from "./components/Toast";
	import Router from './pager/Router.svelte';
  	import Route from './pager/Route.svelte';
  	import RouteNotFound from './pager/RouteNotFound.svelte';
	import AppHelp from './AppHelp.svelte';
	import AppHome from './AppHome.svelte';
    import AppLogo from './AppLogo.svelte'
	import AppMenu from './AppMenu.svelte'
	import Enum from './pages/Enum.svelte';
	import Owner from './pages/Owner.svelte';
	import OwnerViewer from './pages/OwnerViewer.svelte';
	import Pet from './pages/Pet.svelte';
	import PetViewer from './pages/PetViewer.svelte';
	import Vet from './pages/Vet.svelte';
	import VetViewer from './pages/VetViewer.svelte';
	import Visit from './pages/Visit.svelte';
	let menuVisible = false;
</script>

<article class="flex flex-col h-screen">
	<header class="flex justify-between bg-gray-200 p-2 h-12">
		<nav class="flex flex-row text-lg text-gray-600 gap-1">
			<AppMenu bind:open={menuVisible}/>
			<AppLogo bind:open={menuVisible}/>
		</nav>
		<nav class="flex flex-row text-lg text-gray-600 gap-1">
			<a on:click={() => menuVisible = false} href="/help">?</a>
		</nav>
	</header>
	<main class="flex-1 overflow-y-auto">
		<Toast />
		<Menu bind:show={menuVisible}>
			<div class="flex flex-col p-2 text-gray-600 gap-1">
				<span class="text-lg text-gray-900 capitalize">Client</span>
				<div class="flex flex-col p-4 text-gray-600 gap-1">
					<a on:click={() => menuVisible = false} href="/owner">Owner</a>
					<a on:click={() => menuVisible = false} href="/pet">Pet</a>
				</div>
			</div>
			<div class="flex flex-col p-2 text-gray-600 gap-1">
				<span class="text-lg text-gray-900 capitalize">Clinic</span>
				<div class="flex flex-col p-4 text-gray-600 gap-1">
					<a on:click={() => menuVisible = false} href="/visit">Visit</a>
					<a on:click={() => menuVisible = false} href="/vet">Vet</a>
					<a on:click={() => menuVisible = false} href="/enum/skill">Skill</a>
					<a on:click={() => menuVisible = false} href="/enum/species">Species</a>
				</div>
			</div>
		</Menu>
		<Router>
			<Route path="/" 
				component="{AppHome}" />
			<Route path="/home" 
				component="{AppHome}" />
			<Route path="/help" 
				component="{AppHelp}" />
			<Route path="/owner" 
				component="{Owner}" />
			<Route path="/owner/:id"
				component="{OwnerViewer}" />
			<Route path="/pet"
				component="{Pet}" />
			<Route path="/pet/:id"
				component="{PetViewer}" />
			<Route path="/visit" 
				component="{Visit}" />
			<Route path="/vet" 
				component="{Vet}" />
			<Route path="/vet/:id"
				component="{VetViewer}" />
			<Route path="/enum/skill" 
				component="{Enum}" art="skill" />
			<Route path="/enum/species" 
				component="{Enum}" art="species" />
			<RouteNotFound>
				<h1>Ups!</h1>
			</RouteNotFound>
		</Router>
	</main>
	<footer class="flex justify-center bg-gray-200 p-2 h-10">
		<nav class="flex flex-row text-sm text-gray-600 gap-1">
			<a on:click={() => menuVisible = false} href="/help">Impressum</a>
		</nav>
	</footer>
</article>
