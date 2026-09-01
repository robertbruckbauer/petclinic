# Playwright API tests for REST endpoints used by the Angular/Svelte service classes

Source requirements: `plans/create-test-cases-for-rest-endpoints.prompt.md`

## 1. Research summary

Angular (`app/client-angular/src/main/angular/services/*.service.ts`) and Svelte
(`app/client-svelte/src/main/svelte/services/*.service.ts`) each define the same six service
classes, calling the same REST endpoints with the same shapes. Both extend a `BackendService`
that wraps `fetch`; unit tests for each service (`*.service.test.ts`) already cover the
happy path against a mocked `fetch`, URL/query-string construction, and (for `VersionService`)
error/network-failure handling. **This confirms "no per-framework duplication is needed":
one Playwright test per service function is enough to cover both clients.**

Backend endpoints live in `lib/backend-data` (Spring Data REST + a hand-written controller for
`Enum`) and are configured without HAL: collections are `{"content":[...]}`, related entities
are write-only URI references on input and read back as embedded `xItem` summaries
(`{"value":"<uuid>","text":"<label>"}`), never as links. Base path is `/api`.

## 2. Scope: service function → REST endpoint → helper/test mapping

Only functions that are actually called from a UI component or are otherwise part of the public,
REST-calling surface of a service class are in scope (per the prompt's goal: "tests for each
REST endpoint **used in the service classes**").

| Nested test class | Service function | HTTP | Endpoint | `ServerApiAssertion` helper |
|---|---|---|---|---|
| `EnumServiceTests` | `loadAllEnum(art)` | GET | `/api/enum/{art}` | `loadAllEnum(String art)` |
| | `createEnum(art, item)` | POST | `/api/enum/{art}` | `createEnum(String art, String json)` |
| | `updateEnum(art, item)` | PUT | `/api/enum/{art}/{code}` | `updateEnum(String art, long code, String json)` |
| | `removeEnum(art, code)` | DELETE | `/api/enum/{art}/{code}` | `removeEnum(String art, long code)` |
| `OwnerServiceTests` | `loadAllOwner(search)` | GET | `/api/owner` | `loadAllOwner(Map<String,String> query)` |
| | `loadAllOwnerItem()` | GET | `/api/owner/search/findAllItem` | `loadAllOwnerItem()` |
| | `loadOneOwner(id)` | GET | `/api/owner/{id}` | `loadOneOwner(UUID id)` |
| | `createOwner(value)` | POST | `/api/owner` | `createOwner(String json)` |
| | `mutateOwner(id, value)` | PATCH | `/api/owner/{id}` | `mutateOwner(UUID id, String json)` |
| | `removeOwner(id)` | DELETE | `/api/owner/{id}` | `removeOwner(UUID id)` |
| `PetServiceTests` | `loadAllPet(search)` | GET | `/api/pet` | `loadAllPet(Map<String,String> query)` |
| | `loadAllPetItem()` | GET | `/api/pet/search/findAllItem` | `loadAllPetItem()` |
| | `loadOnePet(id)` | GET | `/api/pet/{id}` | `loadOnePet(UUID id)` |
| | `createPet(value)` | POST | `/api/pet` | `createPet(String json)` |
| | `mutatePet(id, value)` | PATCH | `/api/pet/{id}` | `mutatePet(UUID id, String json)` |
| | `removePet(id)` | DELETE | `/api/pet/{id}` | `removePet(UUID id)` |
| `VetServiceTests` | `loadAllVet(search)` | GET | `/api/vet` | `loadAllVet(Map<String,String> query)` |
| | `loadAllVetItem()` | GET | `/api/vet/search/findAllItem` | `loadAllVetItem()` |
| | `loadOneVet(id)` | GET | `/api/vet/{id}` | `loadOneVet(UUID id)` |
| | `createVet(value)` | POST | `/api/vet` | `createVet(String json)` |
| | `mutateVet(id, value)` | PATCH | `/api/vet/{id}` | `mutateVet(UUID id, String json)` |
| | `removeVet(id)` | DELETE | `/api/vet/{id}` | `removeVet(UUID id)` |
| `VisitServiceTests` | `loadAllVisit(search)` | GET | `/api/visit` | `loadAllVisit(Map<String,String> query)` |
| | `loadOneVisit(id)` | GET | `/api/visit/{id}` | `loadOneVisit(UUID id)` |
| | `createVisit(value)` | POST | `/api/visit` | `createVisit(String json)` |
| | `mutateVisit(id, value)` | PATCH | `/api/visit/{id}` | `mutateVisit(UUID id, String json)` |
| | `removeVisit(id)` | DELETE | `/api/visit/{id}` | `removeVisit(UUID id)` |

### Out of scope (with rationale)

- **`VersionService#loadVersion` (`GET /version`)** — already fully exercised by the existing
  `versionApi()` smoke test in `ServerRunnerTest`. Not duplicated as a nested test class.
- **`VersionService#apiExplorerUrl` / `apiGraphiqlUrl`** — pure URL builders, never `fetch`ed by
  the client; not REST calls.
- **`GET /api/pet/{id}/owner`, `GET /api/visit/{id}/pet`, `GET /api/visit/{id}/vet`** — these
  Spring-Data-REST association endpoints exist on the backend and were exercised by the old
  `restApi()`/`assertPet()`/`assertVisit()` tests, but **no Angular or Svelte service function
  calls them** (the UI gets the related-entity summary via the embedded `ownerItem`/`petItem`/
  `vetItem` fields instead). Dropped from scope together with `restApi()`.
- **`Owner.allPet` (write-only, unused by any service function)** — not exercised.

## 3. `ServerApiAssertion.java` design

Keep: constructor, `doWithApi`, `randomName`, `randomInt`, the `jsonMapper` field, imports for
`Owner`/`Pet`/`Vet`/`Visit`/`Enum`.

Add imports for `esy.api.client.OwnerItem`, `esy.api.client.PetItem`, `esy.api.clinic.VetItem`.

Remove: `assertEnumSkill`, `assertEnumSpecies`, `assertOwner`, `assertPet`, `assertVet`,
`assertVisit`, and the private `createOwner/deleteOwner/createPet/deletePet/createVet/deleteVet/
createVisit/deleteVisit` helpers — replaced by the public helpers below, one per service
function. `RequestOptions` stays used **only inside this class**.

For each helper:
- **Load helpers** (`loadAllX(Map<String,String> query)`) build `RequestOptions.create()` and call
  `.setQueryParam(key, value)` for every entry of `query` (mirrors the `Record<string,string>`
  the TS services turn into `HttpParams`/`URLSearchParams`); a `loadAllX()` with an empty map is
  used where the real caller passes no query object. Response parsed via
  `jsonMapper.parseJsonContent(res.text(), X.class)` → `List<X>`.
- **`loadOneX(UUID id)`** — plain `GET`, `jsonMapper.parseJson`/`X.fromJson(res.text())`.
- **`createX(String json)`** — `POST`, header `Content-Type: application/json`, `.setData(json)`,
  asserts `201 Created`, returns parsed entity.
- **`updateEnum(String art, long code, String json)`** — `PUT`, header `Content-Type:
  application/json`, asserts `200 OK`.
- **`mutateX(UUID id, String json)`** — `PATCH`, header `Content-Type:
  application/merge-patch+json`, asserts `200 OK`.
- **`removeX(UUID/art+code id)`** — `DELETE`, asserts `200 OK`, returns the deleted entity (as
  the services expect an echoed body back).

Each helper does the status-code assertion itself (consistent with today's `doWithApi` usage) but
leaves business-field assertions to the calling test — helpers are thin REST-call wrappers, tests
express the expectations.

## 4. `ServerRunnerTest.java` design

- Keep `healthApi()` and `versionApi()` exactly as-is; they stay the first tests
  (`@TestMethodOrder`/declaration order already puts them first).
- Delete `restApi()`.
- Keep `randomName`/`randomInt` at the top (used by every nested class) — move them here from
  `ServerApiAssertion` if not already accessible, or keep both classes with their own copies as
  the existing code already does (`ServerApiAssertion` keeps its own `private`/`protected`
  copies for its internal use, e.g. building bodies with random ids where needed).
- Add five `@Nested` classes, each opening its own `Playwright`/`ServerApiAssertion` per test
  (matching the example), named and ordered as in the scope table above.

### `EnumServiceTests`

- `loadAllEnum()` — calls `assertion.loadAllEnum("skill")` and `assertion.loadAllEnum("species")`
  (the only two `art` values ever used by the UI, from `enum.routes`/`EnumLister` callers) against
  the data seeded by `ServerTestset`; asserts the returned names contain `Radiology`,
  `Dentistry`, `Surgery` (skill) and `Cat`, `Dog`, `Rat`, `Pig`, `Bird` (species) — same
  expectations as today's dropped `assertEnumSkill`/`assertEnumSpecies`.
- `editEnum()` — lifecycle test for `createEnum`/`updateEnum`/`removeEnum`, following the
  prompt's example structure: art `"skill"` (a real value, not `"foo"`), a random `code`
  (1000–9000, to avoid colliding with seeded codes 0–2) and random `name`; create → assert
  fields; update (new `text`) → assert change; `removeEnum` in `finally`.

### `OwnerServiceTests`

- `loadAllOwner()` — real query shape from `OwnerLister`: `{sort:"name,asc", name:"<criteria>"}`.
  Two cases: a name filter matching one seeded owner (e.g. `name="Mann"` → `Thomas Mann`), and
  the empty-filter wildcard case the UI sends when the filter field is blank (`name="%"`) →
  asserts all 4 seeded owners are present, sorted by name.
- `loadAllOwnerItem()` — asserts the seeded owners' items are present (`text` = owner name).
- `loadOneOwner()` — resolve a seeded owner's id via `loadAllOwnerItem()`/`loadAllOwner()`, then
  `loadOneOwner(id)`, assert `name`/`address`/`contact`.
- `editOwner()` — lifecycle test for `createOwner`/`mutateOwner`/`removeOwner`: create a random
  owner; two `mutateOwner` merge-patch calls mirroring `OwnerEditor`'s save (it always sends the
  full merged object as the patch body) — one changing `address`, one changing `contact`; delete
  in `finally`.

### `PetServiceTests`

- `loadAllPet()` — real query shape from `PetLister`: `{sort:"name,asc", "owner.id":"<ownerId>"}`.
  Resolve seeded owner `Thomas Mann`'s id, call with that filter, assert the result contains
  `Tom` and `Odi` (both owned by Thomas Mann) and not `Fox` (owned by Stefan Zweig).
- `loadAllPetItem()` — asserts an item with `text = "Dog 'Odi'"` (the `"{species} '{name}'"`
  pattern) is present.
- `loadOnePet()` — resolve a seeded pet id (e.g. `Odi`), `loadOnePet(id)`, assert
  `name`/`species`/`born`.
- `editPet()` — lifecycle test for `createPet`/`mutatePet`/`removePet`: create a fresh owner
  (via `OwnerServiceTests`-style `createOwner` call — duplicated as a small local setup, not a
  shared cross-class dependency), create a pet referencing it via `"owner":"/api/owner/<id>"`
  (matching `PetEditor`'s body shape, incl. `species`/`sex`/`born`), assert response omits
  `owner` but includes `ownerItem`; `mutatePet` changing `born` (mirrors `PetEditor`'s update);
  delete the pet then the owner in `finally`.

### `VetServiceTests`

- `loadAllVet()` — real query shape from `VetLister`: `{sort:"name,asc", name:"<criteria>"}`.
  Filter for seeded `Eric Idle`, assert single match.
- `loadAllVetItem()` — asserts an item with `text = "Eric Idle"` is present.
- `loadOneVet()` — resolve seeded `Eric Idle`'s id, `loadOneVet(id)`, assert `name`/`allSkill`.
- `editVet()` — lifecycle test for `createVet`/`mutateVet`/`removeVet`: create with random name
  and `allSkill: ["Surgery"]` (mirrors `VetEditor`'s body, incl. `allSpecies: []`); `mutateVet`
  changing `allSkill` (e.g. to `[]`, matching the existing dropped `assertVet` expectation);
  delete in `finally`.

### `VisitServiceTests`

- `loadAllVisit()` — covers both real query shapes for this one function: `VisitLister`'s
  `{sort:"date,desc"}` (assert seeded visits are present, ordered by date descending) and
  `VisitOverview`'s `{sort:"date,desc","pet.owner.id":"<ownerId>"}` using seeded owner
  `Thomas Mann` (assert only visits for `Tom`/`Odi` are returned).
- `loadOneVisit()` — resolve a seeded visit id (e.g. via the `pet.owner.id` query above), assert
  `date`/`text`/`petItem`/`vetItem`.
- `editVisit()` — lifecycle test for `createVisit`/`mutateVisit`/`removeVisit`, reusing seeded
  pet `Odi` and vet `Eric Idle` with `date = LocalDate.now()` (no seeded visit uses today's date,
  avoiding the `(date, pet)` unique constraint): create with `pet`/`vet`/`date` URIs (mirrors
  `VisitTreatment`'s create shape); `mutateVisit` with `text`, `time`, `duration` (ISO‑8601,
  e.g. `"PT30M"`) and a re-pointed `vet` URI (mirrors `VisitDiagnose`'s richer patch shape, the
  most complete real body for this function); delete in `finally`.

## 5. Data strategy

- **Read-only load/loadOne tests** use data already seeded by `ServerTestset` (owners, pets,
  vets, enums, visits) — no setup/teardown needed, matches how the UI queries pre-existing data.
- **Lifecycle tests** (`editX`) create their own random, disposable entities (`randomName`/
  `randomInt`, as in the existing helpers) and clean up in `finally`, so they don't interfere
  with the seeded fixtures the load tests depend on.

## 6. Implementation steps

1. Rewrite `app/server/src/test/java/esy/ServerApiAssertion.java`: remove the `assertX`/private
   create-delete methods, add the per-function helpers from §3.
2. Rewrite `app/server/src/test/java/esy/ServerRunnerTest.java`: remove `restApi()`, add the five
   `@Nested` test classes from §4, keeping `healthApi()`/`versionApi()` first and unchanged.
3. Run `spotlessApply` for the touched test sources (repo convention per
   `app/server/build.gradle`'s `spotless` block, which already covers `src/test/java`).

## 7. Verification

- `./gradlew :app:server:test --tests "esy.ServerRunnerTest"` (Windows: `gradlew.bat`) — all
  smoke tests and all nested service test classes pass.
- `./gradlew :app:server:lint` — Spotless check passes on the modified test files.
