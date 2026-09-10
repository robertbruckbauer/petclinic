# Owner Management

Manage pet owners acting as clients of the clinic. References `rest-conventions`, `graphql-conventions`, `client-shell`, `client-style`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Owner model

An `Owner` has `name` (string, required, unique), `address` (string, required), `contact` (string, required to be present but may be blank), and its pets — exposed read-only as `allPetItem`, never as the raw `allPet` relation, per `rest-conventions`' relation-item rule.

### Requirement: `POST /api/owner` creates an owner

Creates a new `Owner` from `name`, `address`, and `contact`.

Reports `201 Created` with the new entity on success, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken.

### Requirement: `GET /api/owner` lists owners

Returns a paginated, sortable, filterable collection of `Owner`, per `rest-conventions`. `name`, `address`, and `contact` are each filterable as a case-insensitive substring or `LIKE` pattern.

Reports `200 OK`.

#### Scenario: Filtering by name
- **GIVEN** owners named "Max Mustermann" and "Erika Musterfrau"
- **WHEN** `GET /api/owner?name=Max` is requested
- **THEN** only "Max Mustermann" is returned, regardless of case

### Requirement: `GET /api/owner/{id}` gets one owner

Returns the `Owner` with the given id.

Reports `200 OK` if found, `404 Not Found` otherwise.

### Requirement: `GET /api/owner/search/findAllItem` lists owners as items

Returns every `Owner` matching the same filters as `GET /api/owner`, as `OwnerItem` (`value` = id, `text` = `name`), sorted by name, unpaginated — per `rest-conventions`' item-selection rule.

Reports `200 OK`.

### Requirement: `PUT /api/owner/{id}` replaces an owner

Replaces an existing `Owner`'s `name`, `address`, and `contact` wholesale, or creates one at that id if none exists yet.

Reports `200 OK` if updated, `201 Created` if created, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken by another owner, `412 Precondition Failed` on a stale `If-Match` (per `rest-conventions`).

### Requirement: `PATCH /api/owner/{id}` partially updates an owner

Updates only the given fields of an existing `Owner`, per `rest-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the owner does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/owner/{id}` deletes an owner

Deletes the `Owner` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/owner` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allOwner` query

Returns every `Owner`. Returns `[]`, not an error, if none exist.

### Requirement: `ownerById` query

Returns the `Owner` with the given `id`, or `null` if none exists.

### Requirement: `ownerByName` query

Returns the `Owner` with the given `name` (exact match, case-sensitive), or `null` if none exists.

### Requirement: `allPet` relation is batch-loaded

An owner's `allPet` field is resolved via batch loading (per `graphql-conventions`) rather than one query per owner.

### Requirement: `contact` is declared nullable even though it is never actually absent

The schema declares `contact: String` (nullable), but the underlying data always has a value — it defaults to an empty string, never `null`. Treat an empty string, not `null`, as "no contact given."

### Requirement: Authorization

`/graphql` queries touching `Owner` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Owner lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `name`. Editor edits `name`, `address`, `contact` (not `id`/`version`). A pet's owner is navigable from the pet screens, not re-entered as free text.

### Requirement: Owner editor presents Name, Address, and Contact as plain text fields, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `name` | Name | single-line text |
| 2 | `address` | Address | single-line text |
| 3 | `contact` | Contact | single-line text |

All three are required.

### Requirement: Owner lister shows Name and Pets columns, plus per-row visit/pet/edit/delete actions

| Order | Column | Title |
|---|---|---|
| 1 | `name` | Name |
| 2 | `allPet` (names only) | Pets |

Each row additionally carries, in order: "Show all visits" (expands that owner's visits inline, per `visit-management`'s overview), "Add a new pet" (opens a pet-create editor scoped to that owner), "Delete an owner", "Edit an owner". The header's trailing action is titled "Add a new owner".

#### Scenario: Pets column lists the owner's pets by name
- **GIVEN** an owner with two pets
- **WHEN** the owner lister renders that owner's row
- **THEN** the Pets column lists both pet names, and an owner with no pets shows "No pets" instead of an empty cell

### Requirement: Owner viewer currently shows only a minimal identification, not the full field set

Unlike the editor, the dedicated owner viewer route does not yet render `name`/`address`/`contact`/`allPet` individually — the read-only display of those fields happens today inline in the owner lister's row, per `client-shell`.
