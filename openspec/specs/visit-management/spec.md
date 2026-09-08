# Visit Management

Manage visits where a vet examines a pet. References `rest-conventions`, `graphql-conventions`, `client-shell`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Visit exposes date, time, notes, billing info, duration, and its pet/vet

A `Visit` has `date` (date), `time` (time), `text` (notes, string), `billable` (boolean), `duration`, and required relations to exactly one `pet` and one `vet`.

### Requirement: Full CRUD at `/api/visit`

`POST /api/visit`, `GET /api/visit`, `GET /api/visit/{id}`, `PUT /api/visit/{id}`, `PATCH /api/visit/{id}`, `DELETE /api/visit/{id}` behave per `rest-conventions`.

### Requirement: A visit cannot exist without a valid pet and vet reference

#### Scenario: Creating a visit with an unknown pet or vet
- **GIVEN** a `POST /api/visit` request referencing a `pet` or `vet` id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request` (per `rest-conventions`' validation rule)

### Requirement: Authorization

No endpoint under `/api/visit` currently requires authentication — see `security`. (Whether a vet may only edit visits assigned to them is an open question for the future security plan — see `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`.)

## GraphQL Requirements

### Requirement: `allVisit`, `visitById` queries

Per `graphql-conventions`. The `pet` and `vet` relations are resolved via batch loading.

### Requirement: Authorization

`/graphql` queries touching `Visit` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Visit lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `date` + `pet` name (scoped to the current pet or vet when navigated from those screens). Editor edits `date`, `time`, `text`, `billable`, `duration`, and lets the user pick `pet` and `vet` from item-selection lists, never free-text ids.

### Requirement: A visit is created via a Treatment form scheduling the pet's visit, then recorded via a separate Diagnose form

Creating a visit (from the pet screens) only asks for when the visit happens; recording what happened is a separate step, done afterwards on the same visit.

**Treatment form** (create; embedded inline under the pet whose treatment is being scheduled, per `pet-management`):

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `date` | Treatment | date |
| 2 | `time` | Time | time |

`date` is required, `time` is optional.

**Diagnose form** (update; embedded inline under a visit row, and on the visit detail page):

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `text` | Diagnose | multi-line text |
| 2 | `time` | Time | time |
| 3 | `duration` | Duration | three linked number fields, titled "Hours"/"Minutes"/"Seconds" |
| 4 | `vet` | Vet | dropdown, populated from `vet-management`'s item-selection list, placeholder "Choose a vet" |

`text` and `vet` are required; `time` and `duration` are optional.

#### Scenario: Duration is entered as separate hour/minute/second fields
- **GIVEN** a visit's Diagnose form
- **WHEN** the user enters values into the Hours, Minutes, and Seconds fields
- **THEN** they are combined into the visit's single `duration` value on submit, and all-zero is treated as "not set"

### Requirement: Visit lister groups rows by date and shows Owner, Pet, and Vet columns

| Order | Column | Title |
|---|---|---|
| 1 | `pet.owner` | Owner |
| 2 | `pet` | Pet |
| 3 | `vet` | Vet |

Rows are grouped under a heading showing their `date` whenever it differs from the previous row's. The Owner and Vet cells link to those entities' viewers; the Pet cell links to that visit's own detail page. Each row additionally carries, in order: "Delete a visit", "Edit visit details" (opens the Diagnose form inline). There is no header create action on this lister — a visit is always created from the pet screens, per `pet-management`.

### Requirement: Visit overview shows a per-visit read-only card, grouped under an owner

Shown inline under an owner's row in the owner lister ("Show all visits", per `owner-management`). Each card presents, in order: `pet` (titled "Pet"), `date` (titled "Treatment"), `vet` (titled "Vet"), `text` (titled "Diagnose"), all read-only, followed by an "Edit" link to that visit's detail page.

### Requirement: Visit detail page (the `viewer` route) shows a read-only summary plus both forms

The route addressed by a visit's id shows `date`, `time`, and `duration` (formatted as "<n> hours <n> minutes <n> seconds", or "-" when unset) read-only, titled "Date"/"Time"/"Duration" in that order, followed by the Treatment form and the Diagnose form (both editable in place, per the requirement above) so the full visit can be corrected from one page. Unlike `owner-management`/`pet-management`/`vet-management`, this viewer is fully implemented, not a stub.
