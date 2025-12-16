<script lang="ts">
  import "./index.css";
  import Toast from "./controls/Toast";
  import Router from "./router/Router.svelte";
  import Route from "./router/Route.svelte";
  import RouteNotFound from "./router/RouteNotFound.svelte";
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
  import VisitViewer from "./pages/clinic/VisitViewer.svelte";

  let menuVisible = $state(false);
  function onMenuToggle(event: Event) {
    event.stopPropagation();
    menuVisible = !menuVisible;
  }
  function onMenuClose(event: Event) {
    event.stopPropagation();
    menuVisible = false;
  }
</script>

<div class="relative h-screen">
  <header class="z-1 fixed top-0 left-0 right-0 navbar bg-base-100 shadow-sm">
    <div class="navbar-start">
      <details class="dropdown" bind:open={menuVisible} onclick={onMenuToggle}>
        <summary
          tabindex="0"
          class="btn btn-circle swap swap-rotate"
          class:swap-active={menuVisible}
        >
          <svg
            class="swap-off h-5 w-5"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 6h16M4 12h16M4 18h16"
            />
          </svg>
          <svg
            class="swap-on h-5 w-5"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </summary>
        <ul
          tabindex="-1"
          class="menu dropdown-content bg-base-100 rounded-box z-1 mt-3 w-max p-2 shadow"
        >
          <li>
            <span class="capitalize">Client</span>
            <ul class="p-2">
              <li>
                <a class="link" onclick={onMenuClose} href="/owner">Owner</a>
              </li>
              <li>
                <a class="link" onclick={onMenuClose} href="/pet">Pet</a>
              </li>
            </ul>
          </li>
          <li>
            <span class="capitalize">Clinic</span>
            <ul class="p-2">
              <li>
                <a class="link" onclick={onMenuClose} href="/visit">Visit</a>
              </li>
              <li>
                <a class="link" onclick={onMenuClose} href="/vet">Vet</a>
              </li>
              <li>
                <a class="link" onclick={onMenuClose} href="/enum/skill"
                  >Skill</a
                >
              </li>
              <li>
                <a class="link" onclick={onMenuClose} href="/enum/species"
                  >Species</a
                >
              </li>
            </ul>
          </li>
        </ul>
      </details>
    </div>
    <div class="navbar-center">
      <a onclick={onMenuClose} href="/home" class="btn btn-ghost text-xl"
        >Petclinic
      </a>
    </div>
    <div class="navbar-end">
      <a onclick={onMenuClose} href="/help" class="btn btn-ghost text-xl">?</a>
    </div>
  </header>

  <main class="z-2 py-16 h-full overflow-auto">
    <Toast />
    <Router>
      <Route path="/" component={Home} />
      <Route path="/home" component={Home} />
      <Route path="/help" component={Help} />
      <Route path="/owner" component={OwnerLister} />
      <Route path="/owner/:id" component={OwnerViewer} />
      <Route path="/pet" component={PetLister} />
      <Route path="/pet/:id" component={PetViewer} />
      <Route path="/visit" component={VisitLister} />
      <Route path="/visit/:id" component={VisitViewer} />
      <Route path="/vet" component={VetLister} />
      <Route path="/vet/:id" component={VetViewer} />
      <Route path="/enum/skill" component={EnumLister} art="skill" />
      <Route path="/enum/species" component={EnumLister} art="species" />
      <RouteNotFound>
        <h1>Ups!</h1>
      </RouteNotFound>
    </Router>
  </main>

  <footer class="z-1 fixed bottom-0 left-0 right-0 dock">
    <a onclick={onMenuClose} href="/help" class="btn btn-ghost text-xl"
      >Impressum
    </a>
  </footer>
</div>
