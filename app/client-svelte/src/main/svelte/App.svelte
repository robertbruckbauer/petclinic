<script lang="ts">
  import "./index.css";
  import { fly } from "svelte/transition";
  import { quadIn } from "svelte/easing";
  import Toast from "./components/Toast";
  import Router from "./router/Router.svelte";
  import Route from "./router/Route.svelte";
  import RouteNotFound from "./router/RouteNotFound.svelte";
  import AppLogo from "./components/AppLogo";
  import AppIcon from "./components/AppIcon";
  import Home from "./pages/Home.svelte";
  import Help from "./pages/Help.svelte";
  import EnumLister from "./pages/basis/EnumLister.svelte";
  import OwnerLister from "./pages/client/OwnerLister.svelte";
  import OwnerViewer from "./pages/client/OwnerViewer.svelte";
  import PetLister from "./pages/client/PetLister.svelte";
  import PetViewer from "./pages/client/PetViewer.svelte";
  import VetLister from "./pages/clinic/VetLister.svelte";
  import VetViewer from "./pages/clinic/VetViewer.svelte";
  import VisitLister from "./pages/clinic/VisitLister.svelte";

  let menuVisible = $state(false);
  function handleClick() {
    menuVisible = false;
  }
</script>

<article class="flex flex-col h-screen">
  <header class="flex justify-between items-center bg-title-200 p-2 h-12">
    <nav class="flex flex-row text-lg text-title-600 gap-1">
      <AppIcon bind:open={menuVisible} />
      <AppLogo bind:open={menuVisible} />
    </nav>
    <nav class="flex flex-row text-lg text-title-600 gap-1">
      <a onclick={handleClick} href="/help">?</a>
    </nav>
  </header>
  <main class="flex-1 overflow-y-auto">
    <Toast />
    {#if menuVisible}
      <aside
        class="w-72 h-full pointer-events-none"
        transition:fly={{
          duration: 200,
          x: -300,
          easing: quadIn,
          opacity: 1,
        }}
      >
        <nav
          class="absolute flex w-full h-full pointer-events-auto z-10 bg-white"
        >
          <div class="w-full">
            <div class="flex flex-col p-2 text-gray-600 gap-1">
              <span class="text-lg text-gray-900 capitalize">Client</span>
              <div class="flex flex-col p-4 text-gray-600 gap-1">
                <a onclick={handleClick} href="/owner">Owner </a>
                <a onclick={handleClick} href="/pet">Pet</a>
              </div>
            </div>
            <div class="flex flex-col p-2 text-gray-600 gap-1">
              <span class="text-lg text-gray-900 capitalize">Clinic</span>
              <div class="flex flex-col p-4 text-gray-600 gap-1">
                <a onclick={handleClick} href="/visit">Visit </a>
                <a onclick={handleClick} href="/vet">Vet</a>
                <a onclick={handleClick} href="/enum/skill">Skill</a>
                <a onclick={handleClick} href="/enum/species">Species</a>
              </div>
            </div>
          </div>
        </nav>
      </aside>
    {/if}
    <Router>
      <Route path="/" component={Home} />
      <Route path="/home" component={Home} />
      <Route path="/help" component={Help} />
      <Route path="/owner" component={OwnerLister} />
      <Route path="/owner/:id" component={OwnerViewer} />
      <Route path="/pet" component={PetLister} />
      <Route path="/pet/:id" component={PetViewer} />
      <Route path="/visit" component={VisitLister} />
      <Route path="/vet" component={VetLister} />
      <Route path="/vet/:id" component={VetViewer} />
      <Route path="/enum/skill" component={EnumLister} art="skill" />
      <Route path="/enum/species" component={EnumLister} art="species" />
      <RouteNotFound>
        <h1>Ups!</h1>
      </RouteNotFound>
    </Router>
  </main>
  <footer class="flex justify-center bg-title-200 p-2 h-10">
    <nav class="flex flex-row text-sm text-title-600 gap-1">
      <a onclick={handleClick} href="/help">Impressum</a>
    </nav>
  </footer>
</article>
