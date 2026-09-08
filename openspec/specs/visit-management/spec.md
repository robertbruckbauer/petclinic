# Visit Management

Manage visits where a vet examines a pet. References `rest-conventions`, `graphql-conventions`, `client-shell`, `client-style`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Visit model

A `Visit` has `date` (date, required; unique together with `pet`), `time` (time, optional — becomes definitive once treatment begins), `text` (diagnosis notes, string, optional — becomes definitive at the end of treatment), `billable` (boolean, defaults to `false`), `duration` (optional — becomes definitive at the end of treatment), and optional relations to at most one `pet` and one `vet` — a visit may be created as a placeholder (e.g. for a vet on duty) before either is known. Both relations are write-only and read back as embedded items (`petItem`, plus a derived `ownerItem` for the pet's owner, and `vetItem`), per `rest-conventions`' relation-item rule.

### Requirement: `POST /api/visit` creates a visit

Creates a new `Visit` from `date` and, optionally, `time`, `text`, `billable`, `duration`, `pet`, and `vet`.

Reports `201 Created` on success, `400 Bad Request` if validation fails or a given `pet`/`vet` does not exist, `409 Conflict` if the given `pet` already has a visit on that `date`.

#### Scenario: Creating a visit with an unknown pet or vet
- **GIVEN** a `POST /api/visit` request referencing a `pet` or `vet` id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request`

### Requirement: `GET /api/visit` lists visits

Returns a paginated, sortable, filterable collection of `Visit`, per `rest-conventions`. `date` matches exactly for one value, as an inclusive range for two values, or as a set for three or more (e.g. `?date=2021-04-20`, or `?date=2021-04-20&date=2021-04-25` for a range). `pet.id`, `pet.owner.id`, and `vet.id` each match a visit by the referenced entity's id. `text` is filterable as a case-insensitive substring or `LIKE` pattern. `duration` matches an exact ISO-8601 value (e.g. `?duration=PT45M`).

Reports `200 OK`.

### Requirement: `GET /api/visit/{id}` gets one visit

Returns the `Visit` with the given id.

Reports `200 OK` if found, `404 Not Found` otherwise.

### Requirement: `GET /api/visit/{id}/pet` gets a visit's pet

Returns the full `Pet` entity a visit is for.

Reports `200 OK` if the visit exists and has a pet, `404 Not Found` if the visit does not exist or has no pet.

### Requirement: `GET /api/visit/{id}/vet` gets a visit's vet

Returns the full `Vet` entity conducting a visit.

Reports `200 OK` if the visit exists and has a vet, `404 Not Found` if the visit does not exist or has no vet.

### Requirement: `PUT /api/visit/{id}` replaces a visit

Replaces an existing `Visit`'s `date`, `time`, `text`, `billable`, `duration`, `pet`, and `vet` wholesale, or creates one at that id if none exists yet.

Reports `200 OK` if updated, `201 Created` if created, `400 Bad Request` if validation fails, `409 Conflict` if the given `pet` already has a visit on that `date`, `412 Precondition Failed` on a stale `If-Match`.

### Requirement: `PATCH /api/visit/{id}` partially updates a visit

Updates only the given fields of an existing `Visit`, including reassigning `pet` or `vet` alone, per `rest-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the visit does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/visit/{id}` deletes a visit

Deletes the `Visit` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/visit` currently requires authentication — see `security`. (Whether a vet may only edit visits assigned to them is an open question for the future security plan — see `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`.)

## GraphQL Requirements

### Requirement: `allVisit` query

Returns every `Visit`. Returns `[]`, not an error, if none exist. There is no `visitById` query — a single visit is looked up via `allVisitByPetId`/`allVisitByVetId`/`allVisitAt`, or via REST.

### Requirement: `allVisitAt` query

Returns every `Visit` on a given `date`, ordered by date ascending. Returns `[]` if none match.

### Requirement: `allVisitByPetId` query

Returns every `Visit` for a given `pet` id, ordered by date ascending. Returns `[]` if none match.

### Requirement: `allVisitByVetId` query

Returns every `Visit` for a given `vet` id, ordered by date ascending. Returns `[]` if none match.

### Requirement: `pet` and `vet` relations are batch-loaded and nullable

A visit's `pet` and `vet` fields are each resolved via batch loading (per `graphql-conventions`); a visit with no pet or no vet assigned resolves that field as `null` rather than erroring.

### Requirement: `duration` is exposed as a string

`duration` is typed `String!` in the schema, not a numeric type, and uses ISO-8601 duration notation (e.g. `PT45M`, `PT1H30M`) — the same value REST returns.

### Requirement: Authorization

`/graphql` queries touching `Visit` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Visit lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `date` + `pet` name and, unlike the other listers, always shows every visit unscoped — the owner-scoped view is this capability's own overview (below), not the lister. Editor edits `date`, `time`, `text`, and `duration` (across the two forms below) and lets the user pick `pet` and `vet` from item-selection lists, never free-text ids; `billable` is not exposed in either form.

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
