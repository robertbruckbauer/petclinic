# Frontend React Platform

Technology stack for a planned React client at `app/client-react`, generated from `client-shell`, `client-style`, and the per-entity `## UI Requirements` capabilities — not yet implemented. Per `openspec/AGENTS.md`, any capability suffixed `-platform` is technology-specific by design; like `frontend-svelte-platform` and `frontend-angular-platform`, this capability documents the technology itself rather than abstracting over it. `app/client-react` is REST-only — see `graphql-conventions`.

## Building

### Requirement: The app is a React 19 SPA built with Vite, not a meta-framework

There is no Next.js/Remix and no server-side rendering — routing, server state, and forms are supplied by dedicated libraries (below), not a framework runtime. The dev server proxies `/api` and `/version` to the backend (`http://localhost:8080`), making local development same-origin — unlike `frontend-svelte-platform`/`frontend-angular-platform`, which rely on a dev-only port-substitution convention instead of a proxy.

#### Scenario: The dev server is same-origin with the backend
- **GIVEN** the Vite dev server configured with an `/api` and `/version` proxy to the backend
- **WHEN** the app runs locally
- **THEN** browser requests to `/api/**` and `/version` are same-origin from the page's perspective, even though a separate backend process actually serves them

### Requirement: TypeScript covers the whole app; Prettier (not ESLint) enforces formatting

The app is entirely TypeScript. `prettier` formats every file under `src`; there is no ESLint configuration in this module — matching the convention `frontend-svelte-platform`/`frontend-angular-platform` already use.

### Requirement: The container image is a two-stage Docker build serving prebuilt static files via nginx

Stage one (`node:24-alpine`) runs the npm build; stage two (`nginx:1.26-alpine`) serves the resulting static bundle on port 5054 with a `/healthz` endpoint and an SPA fallback to `index.html` for the client-side router — the same packaging shape `frontend-svelte-platform`/`frontend-angular-platform` use, on a port of its own.

## Testing

### Requirement: Vitest (with Testing Library) is the mandatory unit/component-test runner — deliberately no browser end-to-end runner

Unit and component tests run on Vitest with `jsdom`, `@testing-library/react`, and `msw` for HTTP mocking, using the same v8-coverage/JUnit-XML-report setup `frontend-svelte-platform`/`frontend-angular-platform` use. Unlike those two capabilities' Vitest-plus-Playwright pairing, this one deliberately has no Playwright suite and no browser-driven end-to-end coverage — that gap is accepted, not hidden, and is not filled by introducing a different e2e tool.

#### Scenario: No end-to-end test tool is introduced for this client
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new testing-related dependency
- **THEN** it does not add Playwright or any other browser-automation/e2e tool — this client's test suite stays unit/component-only by design

### Requirement: Vitest's v8 provider is the mandatory source of coverage data

Coverage is collected exclusively through Vitest's `--coverage` flag using the `v8` provider, written to `build/coverage`. No separate coverage tool is introduced.

## Styling

### Requirement: Tailwind CSS and daisyUI are the mandatory, exclusive styling stack

The concrete CSS backing `client-style`'s framework-independent visual rules is Tailwind CSS v4 with daisyUI supplying pre-built component classes, compiled via the `@tailwindcss/vite` plugin — the same integration mechanism `frontend-svelte-platform` uses. Every screen and component satisfies `client-style` through this stack, never through a competing CSS framework, a separate component library, or hand-written bespoke CSS duplicating what Tailwind/daisyUI already provide.

#### Scenario: No second styling framework is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new styling- or component-related dependency
- **THEN** Tailwind CSS and daisyUI remain the only styling stack — adding a competing CSS framework or UI component library is a violation of this requirement, not a matter of preference

### Requirement: Every icon comes from `lucide-react`, imported by exactly one module

A single glyph-registry module holds a closed mapping from an action name (e.g. `create`, `edit`, `delete`) to a `lucide-react` icon component, and is the only module in the app allowed to import from `lucide-react`. A component asks for the action name, never the icon component directly, so "the same glyph always means the same action" (per `client-style`) is enforced by the type system, not convention alone.

#### Scenario: A component cannot import an icon directly
- **GIVEN** any component outside the glyph-registry module
- **WHEN** it needs an icon
- **THEN** it imports the named glyph from the registry module — it does not import from `lucide-react` itself

## Navigation

### Requirement: React Router drives client-side navigation, generated from one shared navigation config

A single navigation config module lists every entity's route group; both the router's route table and the nav menu are built from it, so menu and router cannot disagree. Routes are registered directly (no code-split `loadChildren` boundary is required by this requirement).

#### Scenario: The nav menu and the router share one source of truth
- **GIVEN** the navigation config module
- **WHEN** an entity's route group is added or removed there
- **THEN** both the rendered nav menu and the router's route table change accordingly, with no second place to update

## Backend

### Requirement: Backend service classes are fetch-based — no RxJS

Every entity's HTTP calls go through one internal request pipeline built on the native `fetch` API, kept framework-free (no React import, no store, no module-level global) so it is testable in isolation. This is a deliberate divergence from `frontend-svelte-platform`/`frontend-angular-platform`, whose service layers are RxJS-`Observable`-based by mandate — that mandate is scoped to those two capabilities' own modules, not a repository-wide rule, and this capability does not adopt it: server-state and async composition here are TanStack Query's job (below), not RxJS's.

#### Scenario: The service layer has no framework or store dependency
- **GIVEN** the backend service module
- **WHEN** its imports are inspected
- **THEN** it imports no React API, no application store, and no query-library type — it is plain `fetch`-based TypeScript, callable and testable without mounting any component

### Requirement: TanStack Query is the mandatory server-state layer

Every entity's list/item/mutation state is owned by TanStack Query — cache keys, invalidation, and optimistic-vs-refetch behavior are its job, not component-local state. This is the counterpart, for this client, to `frontend-svelte-platform`'s/`frontend-angular-platform`'s localStorage-mirrored store requirement — where those two persist one specific cross-reload value by hand, this client's server data is owned by a dedicated cache layer instead. No other server-state library (e.g. SWR, Redux Toolkit Query, Apollo Client) is introduced alongside or instead of it.

#### Scenario: Entity data lives in the query cache, not component state
- **GIVEN** an entity list rendered by a lister component
- **WHEN** the same entity is refetched elsewhere in the app
- **THEN** every consumer of that query key observes the update — no component holds its own independent copy of the same server data

### Requirement: Editor forms are react-hook-form, validated with zod

Every entity editor is a `react-hook-form`-managed form whose validation schema is a `zod` schema; form validity (`isValid`) and dirtiness (`isDirty`) come from react-hook-form's own state, not hand-rolled tracking. No other form library (e.g. Formik) or hand-rolled validation is introduced alongside or instead of this pair.

#### Scenario: Submit availability is derived from react-hook-form's own state
- **GIVEN** an editor form
- **WHEN** its "Ok"/submit control's disabled state is computed
- **THEN** it reads react-hook-form's `isDirty`/`isValid`/`isSubmitting` state directly, rather than a component tracking those flags itself

### Requirement: ETag optimistic concurrency is derived from `version`, not read from the response header

Because the backend does not expose the `ETag` header cross-origin (it is not in `Access-Control-Expose-Headers`), this client derives the `If-Match` value from each entity's `version` field (the same value the header would otherwise carry) instead of depending on reading `ETag` from the response. A `412` response still forgets any cached value for that resource so the next read re-derives it.

#### Scenario: A mutation still sends `If-Match` without ever reading the `ETag` header
- **GIVEN** an entity loaded with a known `version`
- **WHEN** a `PATCH`/`PUT`/`DELETE` is sent for it
- **THEN** the request carries an `If-Match` value derived from that `version`, regardless of whether the `ETag` response header was readable
