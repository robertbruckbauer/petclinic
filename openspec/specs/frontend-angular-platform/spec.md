# Frontend Angular Platform

Technology stack for the Angular client. Per `doc/arc42/adr/0002-openspec-driven-client-generation.adoc`, this client's target home is its own standalone repository (`petclinic-angular-ui`), not a module of this repository; that repository shares this repository's requirements and scenarios by duplicating — syncing a copied, diffable subset of — this repository's `openspec/` directory into its own, rather than by a live reference (see the ADR for the sync mechanism; this file does not restate it). Until that migration executes, the client lives in-repo at `app/client-angular`, and every requirement below describes its actual, current technology stack regardless of which repository hosts it — see the technical-debt row in `doc/arc42/11-risks-and-technical-debt.adoc` for the tracked gap. Per `openspec/AGENTS.md`, any capability suffixed `-platform` is technology-specific by design — like `backend-platform` and `frontend-svelte-platform`, this capability documents the technology itself rather than abstracting over it. The framework-independent screen inventory and behavioral contract live in `client-shell`, and the concrete visual/interaction rules live in `client-style`; this file does not restate either, only the build/runtime stack that satisfies them today. This client is REST-only — see `graphql-conventions`.

## Building

### Requirement: The app is a standalone-component Angular 22 SPA built with the esbuild-based application builder, not SSR

There are no `NgModule`s — every component is a standalone component, bootstrapped via `bootstrapApplication(App, appConfig)`. Change detection runs without Zone.js (`provideZonelessChangeDetection()`); component-local reactive state uses Angular Signals (`signal()`), not `NgZone`-triggered checks. No `@angular/ssr` dependency is present and no server entry point exists — the app is client-rendered only, despite Angular's newer builder producing an output layout that looks SSR-shaped (see the packaging requirement below). `angular.json`'s `build` target uses `@angular/build:application` (the modern, esbuild-based builder), not the legacy `@angular-devkit/build-angular:browser`; the `serve` target (`@angular/build:dev-server`) and `test` target (`@angular/build:unit-test`, wired to the `vitest` runner) are its counterparts for local development and testing.

#### Scenario: Bootstrapping does not register an NgModule
- **GIVEN** `main.ts`
- **WHEN** the application starts
- **THEN** it calls `bootstrapApplication` with a root component and a providers array (`app.config.ts`) — no `platformBrowserDynamic().bootstrapModule(...)` call exists anywhere in the source

#### Scenario: A production build applies output hashing
- **GIVEN** the `production` build configuration (the default when none is specified)
- **WHEN** `npm run build` (`ng build`) runs
- **THEN** it applies `outputHashing: all` and writes to `build/generated`, distinct from the unoptimized, source-mapped `development` configuration used by `ng serve`

### Requirement: Cross-reload UI state is held in an Angular Signal mirrored to `localStorage`

State that must survive a page reload (e.g. the currently selected owner) is kept in a module-level Signal, initialized from `localStorage` on load and re-persisted to `localStorage` on every `set`, exposed through plain getter/setter functions rather than an injectable service — the same cross-reload persistence pattern `frontend-svelte-platform` uses with a Svelte store, expressed with Angular's own primitive.

### Requirement: TypeScript covers the whole app under Angular's strict template/DI checks; Prettier (not ESLint) enforces formatting

The app is entirely TypeScript, compiled under `strict` plus Angular-specific strictness (`strictTemplates`, `strictInjectionParameters`, `strictInputAccessModifiers`, `noImplicitOverride`). `experimentalDecorators` is enabled for `@Component`/`@Injectable`. `prettier` formats every file under `src`; there is no ESLint configuration in this module.

### Requirement: The container image is a two-stage Docker build serving prebuilt static files via nginx, from the builder's `browser` output folder

Stage one (`node:24-alpine`) runs `npm ci` and `npm run build`. Stage two (`nginx:1.26-alpine`) serves the build output on port 5052 with gzip and ETag enabled, exposes `/healthz` returning a bare `200`, and falls back every unmatched path to `index.html` so the Angular Router can resolve it client-side. Because `@angular/build:application` always emits its browser bundle under a `browser/` subfolder of the configured output path (regardless of whether server-side rendering is used), the image copies `build/generated/browser` — not `build/generated` itself, unlike `frontend-svelte-platform`'s Vite output. This image, too, is built with a plain `docker build`/`docker tag` invocation, not Jib or Cloud Native Buildpacks.

#### Scenario: SPA fallback lets the Angular Router own deep links
- **GIVEN** a request for a path that is not a static asset (e.g. `/owner/42`)
- **WHEN** nginx serves it
- **THEN** it returns `index.html` with `200`, and the Angular Router then resolves the route from the URL client-side

#### Scenario: The health check succeeds independently of the backend
- **GIVEN** the nginx container is running
- **WHEN** `/healthz` is requested
- **THEN** it returns `200` with an empty body regardless of whether the backend is reachable — matching the `depends_on: condition: service_healthy` check `app/deploy`'s Compose topology uses for this service

## Testing

### Requirement: Vitest is the mandatory unit-test runner, Playwright the mandatory browser end-to-end runner

`npm run test` runs `vitest run --coverage` directly (the same Vitest + v8-coverage + JUnit-XML-report setup `frontend-svelte-platform` uses), covering `services/` and `stores/`; `angular.json` additionally wires vitest as the runner behind the `@angular/build:unit-test` architect target for `ng test`. Playwright drives end-to-end journeys against a running build (`baseURL` `http://localhost:5052`); only its Chromium project is enabled today — the Firefox and Safari project definitions exist in `playwright.config.ts` but are commented out. This is not an incidental choice: unit/component-level tests must run on Vitest and browser-driven end-to-end tests must run on Playwright — no other test runner (e.g. Jasmine/Karma, Jest, Cypress, WebdriverIO) is introduced alongside or instead of them.

#### Scenario: End-to-end tests run only against Chromium today
- **GIVEN** `playwright.config.ts`'s `projects` list
- **WHEN** `npm run e2e` runs
- **THEN** only the `client-angular-chromium` project executes

#### Scenario: A new unit test is written for Vitest, not a competing runner
- **GIVEN** a new service or store needs test coverage
- **WHEN** its test file is added
- **THEN** it is written against Vitest's API (`describe`/`it`/`expect` from `vitest`) and matched by `vitest.config.ts`'s `include` pattern — the Angular CLI's own `@angular/build:unit-test` architect target stays wired to the Vitest runner rather than switching to Karma/Jasmine, and no other unit-test framework is added

#### Scenario: A new browser journey is automated with Playwright, not a competing tool
- **GIVEN** a new end-to-end user journey needs coverage
- **WHEN** its test is added under `src/test/playwright`
- **THEN** it is written against `@playwright/test` and follows the existing page-object pattern — no other browser-automation tool is introduced to drive it

#### Scenario: No second test-runner dependency is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new testing-related dependency
- **THEN** Vitest and Playwright remain the only test runners — adding a competing unit-test or e2e-test framework is a violation of this requirement, not a matter of preference

### Requirement: Vitest's v8 provider is the mandatory source of coverage data

Coverage is collected exclusively through Vitest's built-in `--coverage` flag (`npm run test` runs `vitest run --coverage`), using the `v8` provider configured in `vitest.config.ts` (`text`/`html` reporters, written to `build/coverage`) — the same coverage setup `frontend-svelte-platform` uses. This is not incidental: coverage numbers for this module come from Vitest and nowhere else — no separate coverage tool (e.g. Istanbul/`nyc` run standalone, a Playwright coverage plugin, or Angular's legacy Karma coverage reporter) is introduced to measure or report it.

#### Scenario: Running the test script also produces a coverage report
- **GIVEN** `npm run test` is invoked
- **WHEN** Vitest runs
- **THEN** it also collects `v8` coverage and writes `text`/`html` reports to `build/coverage`, with no separate coverage command required

#### Scenario: No second coverage tool is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new coverage-related dependency
- **THEN** Vitest's `v8` coverage provider remains the only source of coverage data for this module — adding a second coverage tool (e.g. running `nyc`/Istanbul separately, a Playwright coverage plugin, or Karma's coverage reporter) is a violation of this requirement, not a matter of preference

## Styling

### Requirement: Tailwind CSS and daisyUI are the mandatory, exclusive styling stack, applied via PostCSS

The concrete CSS backing `client-style`'s framework-independent visual rules is Tailwind CSS v4 with daisyUI's component classes on top, integrated through the Angular builder's PostCSS pipeline (`@tailwindcss/postcss` configured in `.postcssrc.json`) rather than through a Vite plugin — unlike `frontend-svelte-platform`, which applies Tailwind via `@tailwindcss/vite` directly. The resulting visual output is the same shared theme; only the build integration differs per toolchain. This is not an incidental choice: every screen and component must satisfy `client-style` through Tailwind utility classes and daisyUI component classes, never through a competing CSS framework, a separate component library (e.g. Angular Material), or hand-written bespoke CSS that duplicates what Tailwind/daisyUI already provide.

#### Scenario: A new screen styles itself with Tailwind utilities and daisyUI classes only
- **GIVEN** a new page or component is added to the app
- **WHEN** its template is styled
- **THEN** it composes Tailwind utility classes and daisyUI component classes; it does not add hand-written custom CSS rules to reproduce something Tailwind or daisyUI already provides

#### Scenario: No second styling framework is introduced
- **GIVEN** `package.json`'s dependencies
- **WHEN** a change adds a new styling- or component-related dependency
- **THEN** Tailwind CSS and daisyUI remain the only styling stack — adding a competing CSS framework or UI component library is a violation of this requirement, not a matter of preference

## Navigation

### Requirement: The Angular Router drives client-side navigation, with lazy-loaded feature routes and input binding

Unlike `frontend-svelte-platform`'s hand-rolled router, this client uses `@angular/router` directly: `provideRouter(routes, withComponentInputBinding())` in `app.config.ts`, and each entity's route group (`owner`, `pet`, `vet`, `visit`, `enum`) is registered via `loadChildren` pointing at a per-feature `*.routes.ts` file, loaded as a separate chunk on first navigation rather than bundled into the initial load. `withComponentInputBinding()` means a route's path/query parameters are bound directly to matching `@Input()`s on the routed component instead of being read manually from `ActivatedRoute`.

#### Scenario: Navigating to a feature route loads its chunk on demand
- **GIVEN** the app has just booted and no feature route has been visited yet
- **WHEN** the user navigates to `/owner`
- **THEN** the `owner.routes.ts` module (and the components it references) is fetched as a separate chunk at that point, not included in the initial bundle

#### Scenario: An unmatched path renders the not-found page
- **GIVEN** a path that matches none of the registered routes
- **WHEN** the router resolves it
- **THEN** the wildcard (`**`) route lazy-loads and renders `NotFoundComponent`

## Backend

### Requirement: Backend service classes are RxJS-based — mandatory for REST, optional for GraphQL on complex queries

Every entity's service (`owner.service.ts`, `pet.service.ts`, `vet.service.ts`, `visit.service.ts`, `enum.service.ts`) extends `BackendService`, which wraps Angular's injected `HttpClient` for GET/GET-all/POST/PUT/PATCH/DELETE, using `HttpParams` for query strings and mapping both HTTP error responses and `HttpErrorResponse` network failures (`status === 0`) onto the same `ErrorItem`-shaped `Observable` error channel — not a generated API client. This is not incidental: every REST-backed service method must expose its result as an RxJS `Observable`, the same convention `frontend-svelte-platform` uses. Neither client issues GraphQL operations today — GraphQL is currently backend-only (see `graphql-conventions`) — but a service is free to use it for a complex, multi-relation read that a single REST call can't express economically; if one does, that service must still expose its result as an RxJS `Observable`, not a separate async style.

#### Scenario: The backend origin is derived from the dev port convention
- **GIVEN** the app is served from `http://<host>:5052`
- **WHEN** a service computes the backend's origin
- **THEN** it reuses the page's own protocol and host with port `5052` replaced by `8080` — the same dev/local convention `frontend-svelte-platform` uses for its own port, per `app/deploy`'s Compose files

#### Scenario: A network failure surfaces the same way as an HTTP error
- **GIVEN** a request fails before any HTTP response is received
- **WHEN** the calling service subscribes to the resulting `Observable`
- **THEN** it receives an `ErrorItem` with `status: 0`, not an unhandled `HttpErrorResponse` or a thrown exception

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
