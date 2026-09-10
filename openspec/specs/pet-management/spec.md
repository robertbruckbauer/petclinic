# Pet Management

Manage pets belonging to an owner. References `rest-conventions`, `graphql-conventions`, `client-shell`, `client-style`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Pet model

A `Pet` has `name` (string, required, unique per owner — not globally), `born` (date, required), `species` (string, required), `sex` (enum `M`/`F`, required), and its owner — a required relation, write-only and read back as `ownerItem`, per `rest-conventions`' relation-item rule.

### Requirement: `POST /api/pet` creates a pet

Creates a new `Pet` from `name`, `born`, `species`, `sex`, and a reference to its `owner`.

Reports `201 Created` on success, `400 Bad Request` if validation fails or `owner` references an entity that does not exist, `409 Conflict` if `name` is already taken by another pet of the same owner.

#### Scenario: Creating a pet with an unknown owner
- **GIVEN** a `POST /api/pet` request referencing an `owner` id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request`

### Requirement: `GET /api/pet` lists pets

Returns a paginated, sortable, filterable collection of `Pet`, per `rest-conventions`. `name`, `species`, and `owner.name` are each filterable as a case-insensitive substring or `LIKE` pattern; `sex` matches its exact code (`M` or `F`).

Reports `200 OK`.

### Requirement: `GET /api/pet/{id}` gets one pet

Returns the `Pet` with the given id.

Reports `200 OK` if found, `404 Not Found` otherwise.

### Requirement: `GET /api/pet/search/findAllItem` lists pets as items

Returns every `Pet` matching the same filters as `GET /api/pet`, as `PetItem` (`value` = id, `text` = `"<species> '<name>'"`), sorted by name, unpaginated.

Reports `200 OK`.

### Requirement: `GET /api/pet/{id}/owner` gets a pet's owner

Returns the full `Owner` entity a pet belongs to — the way to read the relation itself rather than the embedded `ownerItem`.

Reports `200 OK` if the pet exists, `404 Not Found` otherwise.

### Requirement: `PUT /api/pet/{id}` replaces a pet

Replaces an existing `Pet`'s `name`, `born`, `species`, `sex`, and `owner` wholesale, or creates one at that id if none exists yet.

Reports `200 OK` if updated, `201 Created` if created, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken by another pet of the same owner, `412 Precondition Failed` on a stale `If-Match`.

### Requirement: `PATCH /api/pet/{id}` partially updates a pet

Updates only the given fields of an existing `Pet`, including reassigning `owner` alone, per `rest-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the pet does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/pet/{id}` deletes a pet

Deletes the `Pet` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/pet` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allPet` query

Returns every `Pet`. Returns `[]`, not an error, if none exist.

### Requirement: `petById` query

Returns the `Pet` with the given `id`, or `null` if none exists.

### Requirement: `owner` relation is batch-loaded

A pet's `owner` field is resolved via batch loading (per `graphql-conventions`) rather than one query per pet.

### Requirement: Authorization

`/graphql` queries touching `Pet` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Pet lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `name` (scoped to the current owner when navigated from the owner screens). Editor edits `name`, `born`, `species`, `sex`, and lets the user pick an `owner` from an item-selection list (`owner-management`'s `findAllItem` endpoint), never a free-text owner id.

### Requirement: Pet editor presents Species, Sex, Name, and Born, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `species` | Species | dropdown, populated from the `species` picklist (`enum-management`); each option's tooltip shows that species' descriptive text |
| 2 | `sex` | Sex | dropdown, fixed choice of `male`/`female` |
| 3 | `name` | Name | single-line text |
| 4 | `born` | Born | date |

All four are required. `owner` is not edited on this form: a pet created from the pet lister is scoped to the owner currently selected in the lister's filter; a pet created from the owner screens is scoped to that owner.

### Requirement: Pet lister requires an owner to be chosen before listing, and shows Species and Name columns

The lister's filter is a required dropdown of owners (`owner-management`'s item-selection list, placeholder "Choose an owner"), not a free-text criteria box — the exception to `client-style`'s default filter convention. No pet is requested until an owner is chosen.

| Order | Column | Title |
|---|---|---|
| 1 | `species` | Species |
| 2 | `name` | Name |

Each row additionally carries, in order: "Add a treatment" (opens a visit-create Treatment form for that pet, per `visit-management`), "Delete a pet", "Edit a pet". The header's trailing action is titled "Add a new pet" and stays disabled until an owner is chosen.

#### Scenario: No pets are loaded before an owner is selected
- **GIVEN** the pet lister has just opened with no owner selected
- **WHEN** the page renders
- **THEN** no pet is shown and "Add a new pet" is disabled until an owner is chosen from the filter

### Requirement: Pet viewer currently shows only a minimal identification, not the full field set

Unlike the editor, the dedicated pet viewer route does not yet render `name`/`born`/`species`/`sex`/`owner` individually — the read-only display of those fields happens today inline in the pet lister's row, per `client-shell`.
