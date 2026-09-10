# Frontend Svelte Platform

Technology stack for the current in-repo Svelte client (`app/client-svelte`), as implemented today. Per `openspec/AGENTS.md`, any capability suffixed `-platform` is technology-specific by design — like `backend-platform`, this capability documents the technology itself rather than abstracting over it. The framework-independent screen inventory and behavioral contract live in `client-shell`, and the concrete visual/interaction rules live in `client-style`; this file does not restate either, only the build/runtime stack that satisfies them today. `app/client-svelte` is REST-only — see `graphql-conventions`.

## Requirements

### Requirement: The app is a plain Svelte 5 + Vite single-page app, not a meta-framework

Svelte 5 components (using runes — `$props()`, `$state`, snippet rendering via `{@render}`) are compiled and bundled by Vite through `@sveltejs/vite-plugin-svelte`. There is no SvelteKit, no server-side rendering, and no file-based routing — routing, persisted state, and API access are hand-rolled (see below), not framework-provided. Source lives under `src/main/svelte` and is preprocessed with `vitePreprocess`; the production build is emitted to `build/generated`.

#### Scenario: Building produces a static asset bundle, not a server
- **GIVEN** the project's Vite configuration
- **WHEN** `npm run build` runs
- **THEN** it emits a static asset bundle to `build/generated` — no Node server process is required to render a page

### Requirement: A hand-rolled router drives client-side navigation from the History API

No routing library is used. `router/router.ts` matches `window.location.pathname` against explicitly `register`-ed path patterns (`:param` segments, and a `*` wildcard as the fallback), keeps the currently active route in a Svelte store, intercepts same-origin link clicks to call `history.pushState` instead of a full navigation, and re-resolves the active route on `popstate`.

#### Scenario: An internal link navigates without a full page load
- **GIVEN** the router has been started and a link targets another registered, same-origin route
- **WHEN** the user clicks that link
- **THEN** the browser does not reload the page; history is pushed and the active-route store updates to the new route's component

#### Scenario: An unmatched path falls back to the wildcard route
- **GIVEN** no registered path matches the current pathname
- **WHEN** the router resolves the active route
- **THEN** it activates the `*` route (`RouteNotFound`) instead of leaving no route active

### Requirement: Backend service classes are RxJS-based — mandatory for REST, optional for GraphQL on complex queries

Every entity's service (`owner.service.ts`, `pet.service.ts`, `vet.service.ts`, `visit.service.ts`, `enum.service.ts`) extends `BackendService`, which wraps the native `fetch` API in RxJS `Observable`s for GET/GET-all/POST/PUT/PATCH/DELETE, decodes JSON response bodies, and routes both non-2xx HTTP responses and network-level failures (e.g. the request never reaches a server) through the same `Observable` error channel — not a generated API client. This is not incidental: every REST-backed service method must expose its result as an RxJS `Observable`. Neither client issues GraphQL operations today — GraphQL is currently backend-only (see `graphql-conventions`) — but a service is free to use it for a complex, multi-relation read that a single REST call can't express economically; if one does, that service must still expose its result as an RxJS `Observable`, following the same convention as the REST-based services, not a separate async style.

#### Scenario: The backend origin is derived from the dev port convention
- **GIVEN** the app is served from `http://<host>:5050`
- **WHEN** a service computes the backend's origin
- **THEN** it reuses the page's own protocol and host with port `5050` replaced by `8080` — the dev/local topology assumes client and server share a host and differ only by port, per `app/deploy`'s Compose files

#### Scenario: A network failure surfaces the same way as an HTTP error
- **GIVEN** a request fails before any HTTP response is received
- **WHEN** the calling service subscribes to the resulting `Observable`
- **THEN** it receives an error object shaped like a REST error body, not an unhandled promise rejection or a thrown exception

#### Scenario: Every REST service method returns an Observable
- **GIVEN** any method on an entity service that calls the backend (e.g. `loadOneOwner`, `createOwner`)
- **WHEN** its return type is inspected
- **THEN** it is `Observable<T>` or `Observable<T[]>` — never a `Promise` or a plain callback signature

#### Scenario: A GraphQL-backed service for a complex query still returns an Observable
- **GIVEN** a new service method is added to run a GraphQL query for a complex read that REST cannot express economically
- **WHEN** it is implemented
- **THEN** it wraps the query result in an RxJS `Observable`, matching the return-type convention every REST service method already follows, rather than a `Promise`-based or GraphQL-client-specific async API

#### Scenario: No second async/reactive library is introduced for service classes
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new dependency for asynchronous data fetching, REST or GraphQL
- **THEN** RxJS remains the only reactive library service classes are built on — adding a second one, or making `async`/`await` the primary service API instead of returning an `Observable`, is a violation of this requirement, not a matter of preference

### Requirement: Cross-reload UI state is held in Svelte stores mirrored to `localStorage`

State that must survive a page reload (e.g. the currently selected owner) is kept in a Svelte `writable` store that is initialized from `localStorage` on load and re-persisted to `localStorage` on every change, rather than being re-fetched from the backend or lost on refresh.

### Requirement: Tailwind CSS and DaisyUI are the mandatory, exclusive styling stack

The concrete CSS backing `client-style`'s framework-independent visual rules (color roles, control shapes, spacing, breakpoints) is Tailwind CSS v4, compiled via the `@tailwindcss/vite` plugin, with DaisyUI supplying pre-built component classes on top of it. This is not an incidental choice: every screen and component must satisfy `client-style` through Tailwind utility classes and DaisyUI component classes, never through a competing CSS framework, a separate component library, or hand-written bespoke CSS that duplicates what Tailwind/DaisyUI already provide.

#### Scenario: A new screen styles itself with Tailwind utilities and DaisyUI classes only
- **GIVEN** a new page or component is added to the app
- **WHEN** its markup is styled
- **THEN** it composes Tailwind utility classes and DaisyUI component classes; it does not add hand-written custom CSS rules to reproduce something Tailwind or DaisyUI already provides

#### Scenario: No second styling framework is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new styling- or component-related dependency
- **THEN** Tailwind CSS and DaisyUI remain the only styling stack — adding a competing CSS framework or UI component library is a violation of this requirement, not a matter of preference

### Requirement: TypeScript covers application logic; Prettier (not ESLint) enforces formatting

Services, stores, the router, and type definitions are TypeScript under `strict` compiler settings, with `verbatimModuleSyntax` forcing `import type` for type-only imports; `.svelte` files and `app.js` are type-checked as JS/Svelte via `checkJs`. `prettier` with `prettier-plugin-svelte` formats every `.js`/`.ts`/`.svelte` file under `src`; there is no ESLint configuration in this module. The Gradle `lint`/`format` tasks delegate to `prettierCheck`/`prettierApply`.

### Requirement: Vitest is the mandatory unit-test runner, Playwright the mandatory browser end-to-end runner

Vitest exercises the router, services, and stores under Node, with v8 coverage and a JUnit XML report for CI consumption. Playwright drives full end-to-end journeys against a running build (`baseURL` `http://localhost:5050`) using a page-object pattern under `src/test/playwright`; only its Chromium project is enabled today — the Firefox and Safari project definitions exist in `playwright.config.ts` but are commented out. This is not an incidental choice: unit/component-level tests must run on Vitest and browser-driven end-to-end tests must run on Playwright — no other test runner (e.g. Jasmine/Karma, Jest, Cypress, WebdriverIO) is introduced alongside or instead of them.

#### Scenario: End-to-end tests run only against Chromium today
- **GIVEN** `playwright.config.ts`'s `projects` list
- **WHEN** `npm run e2e` runs
- **THEN** only the `client-svelte-chromium` project executes

#### Scenario: A new unit test is written for Vitest, not a competing runner
- **GIVEN** a new service, store, or router module needs test coverage
- **WHEN** its test file is added
- **THEN** it is written against Vitest's API (`describe`/`it`/`expect` from `vitest`) and matched by `vitest.config.ts`'s `include` pattern — no other unit-test framework is added to run it

#### Scenario: A new browser journey is automated with Playwright, not a competing tool
- **GIVEN** a new end-to-end user journey needs coverage
- **WHEN** its test is added under `src/test/playwright`
- **THEN** it is written against `@playwright/test` and follows the existing page-object pattern — no other browser-automation tool is introduced to drive it

#### Scenario: No second test-runner dependency is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new testing-related dependency
- **THEN** Vitest and Playwright remain the only test runners — adding a competing unit-test or e2e-test framework is a violation of this requirement, not a matter of preference

### Requirement: Vitest's v8 provider is the mandatory source of coverage data

Coverage is collected exclusively through Vitest's built-in `--coverage` flag (`npm run test` runs `vitest run --coverage`), using the `v8` provider configured in `vitest.config.ts` (`text`/`html` reporters, written to `build/coverage`). This is not incidental: coverage numbers for this module come from Vitest and nowhere else — no separate coverage tool (e.g. Istanbul/`nyc` run standalone, a Playwright coverage plugin) is introduced to measure or report it.

#### Scenario: Running the test script also produces a coverage report
- **GIVEN** `npm run test` is invoked, directly or via the Gradle `npmCheck` task
- **WHEN** Vitest runs
- **THEN** it also collects `v8` coverage and writes `text`/`html` reports to `build/coverage`, with no separate coverage command required

#### Scenario: Gradle republishes Vitest's coverage report rather than generating its own
- **GIVEN** the `coverageReport` Gradle task
- **WHEN** it runs after `npmCheck`
- **THEN** it copies Vitest's own `build/coverage` output into `pages/html/client-svelte/coverage` — it does not invoke a separate coverage tool to produce that report

#### Scenario: No second coverage tool is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new coverage-related dependency
- **THEN** Vitest's `v8` coverage provider remains the only source of coverage data for this module — adding a second coverage tool (e.g. running `nyc`/Istanbul separately, or a Playwright coverage plugin) is a violation of this requirement, not a matter of preference

### Requirement: The npm toolchain is wrapped by Gradle, and skipped on the Node-less CI job

The `com.github.node-gradle.node` plugin runs `npm install`/`npm run build`/`npm run test` as part of this module's Gradle `assemble`/`build`/`check` tasks. Every one of those Gradle tasks is guarded by `onlyIf { System.env['CI'] == null }`, so the Java-focused CI job (which has no Node runtime) skips them entirely rather than failing.

#### Scenario: The CI job for the Java build does not attempt to run npm
- **GIVEN** the `CI` environment variable is set
- **WHEN** the Gradle build for `app/client-svelte` runs
- **THEN** `npmInstall`, `npmBuild`, and `npmCheck` are all skipped

### Requirement: The container image is a two-stage Docker build serving prebuilt static files via nginx

Stage one (`node:24-alpine`) runs `npm ci` and `npm run build`. Stage two (`nginx:1.26-alpine`) serves the resulting `build/generated` directory on port 5050 with gzip and ETag enabled, exposes `/healthz` returning a bare `200`, and falls back every other unmatched path to `index.html` so the in-app router can resolve it client-side. Unlike `app/server`/`app/migrate`, this image is built with a plain `docker build`/`docker tag` invocation from Gradle, not Jib or Cloud Native Buildpacks.

#### Scenario: SPA fallback lets the client-side router own deep links
- **GIVEN** a request for a path that is not a static asset (e.g. `/owner/42`)
- **WHEN** nginx serves it
- **THEN** it returns `index.html` with `200`, and the hand-rolled router then resolves the route from the URL client-side

#### Scenario: The health check succeeds independently of the backend
- **GIVEN** the nginx container is running
- **WHEN** `/healthz` is requested
- **THEN** it returns `200` with an empty body regardless of whether the backend is reachable — matching the `depends_on: condition: service_healthy` check `app/deploy`'s Compose topology uses for this service
