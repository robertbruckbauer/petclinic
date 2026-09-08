# Pet Management

Manage pets belonging to an owner. References `rest-conventions`, `graphql-conventions`, `client-shell`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Pet exposes name, birth date, species, sex, and its owner

A `Pet` has `name` (string), `born` (date), `species` (string), `sex` (enum), and a required relation to exactly one `owner`.

### Requirement: Full CRUD at `/api/pet`

`POST /api/pet`, `GET /api/pet`, `GET /api/pet/{id}`, `PUT /api/pet/{id}`, `PATCH /api/pet/{id}`, `DELETE /api/pet/{id}` behave per `rest-conventions`.

### Requirement: A pet cannot exist without a valid owner reference

#### Scenario: Creating a pet with an unknown owner
- **GIVEN** a `POST /api/pet` request referencing an owner id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request` (per `rest-conventions`' validation rule)

### Requirement: Authorization

No endpoint under `/api/pet` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allPet`, `petById` queries

Per `graphql-conventions`. The `owner` relation is resolved via batch loading.

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
