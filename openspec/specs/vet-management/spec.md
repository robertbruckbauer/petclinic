# Vet Management

Manage veterinarians and the skills/species they handle. References `rest-conventions`, `graphql-conventions`, `client-shell`, `client-style`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Vet model

A `Vet` has `name` (string, required, unique) and two collections, `allSkill` and `allSpecies` (each a sorted set of non-blank strings; uniqueness within each set is enforced at the application layer, not by a database constraint).

### Requirement: `POST /api/vet` creates a vet

Creates a new `Vet` from `name`, `allSkill`, and `allSpecies`.

Reports `201 Created` on success, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken.

### Requirement: `GET /api/vet` lists vets

Returns a paginated, sortable, filterable collection of `Vet`, per `rest-conventions`. `name` is filterable as a case-insensitive substring or `LIKE` pattern; `allSkill` and `allSpecies` are filterable the same way, matching a vet that has at least one matching entry.

Reports `200 OK`.

### Requirement: `GET /api/vet/{id}` gets one vet

Returns the `Vet` with the given id.

Reports `200 OK` if found, `404 Not Found` otherwise.

### Requirement: `GET /api/vet/search/findAllItem` lists vets as items

Returns every `Vet` matching the same filters as `GET /api/vet`, as `VetItem` (`value` = id, `text` = `name`), sorted by name, unpaginated.

Reports `200 OK`.

### Requirement: `PUT /api/vet/{id}` replaces a vet

Replaces an existing `Vet`'s `name`, `allSkill`, and `allSpecies` wholesale, or creates one at that id if none exists yet.

Reports `200 OK` if updated, `201 Created` if created, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken by another vet, `412 Precondition Failed` on a stale `If-Match`.

### Requirement: `PATCH /api/vet/{id}` partially updates a vet

Updates only the given fields of an existing `Vet`; updating `allSkill` or `allSpecies` replaces that collection wholesale, per `rest-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the vet does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/vet/{id}` deletes a vet

Deletes the `Vet` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/vet` currently requires authentication — see `security`. (The future security plan, `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`, is expected to give this capability the tightest read/write restriction of the four, since it's clinic-internal data — not decided yet.)

## GraphQL Requirements

### Requirement: `allVet` query

Returns every `Vet`. Returns `[]`, not an error, if none exist.

### Requirement: `vetById` query

Returns the `Vet` with the given `id`, or `null` if none exists.

### Requirement: `vetByName` query

Returns the `Vet` with the given `name` (exact match, case-sensitive), or `null` if none exists.

### Requirement: Authorization

`/graphql` queries touching `Vet` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Vet lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `name`. Editor edits `name` and lets the user add/remove entries in `allSkill` and `allSpecies` as tag-style multi-value inputs, not a single free-text field.

### Requirement: Vet editor presents Name, Skills, and Species, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `name` | Name | single-line text |
| 2 | `allSkill` | Skills | multi-select, all options shown at once, populated from the `skill` picklist (`enum-management`) |
| 3 | `allSpecies` | Species | multi-select, all options shown at once, populated from the `species` picklist (`enum-management`) |

All three are required.

### Requirement: Vet lister shows Name and Skills columns, plus per-row edit/delete actions

| Order | Column | Title |
|---|---|---|
| 1 | `name` | Name |
| 2 | `allSkill` | Skills |

A vet with no skills shows "No skills" instead of an empty cell. Each row additionally carries, in order: "Delete a vet", "Edit a vet". The header's trailing action is titled "Add a new vet".

### Requirement: Vet viewer currently shows only a minimal identification, not the full field set

Unlike the editor, the dedicated vet viewer route does not yet render `name`/`allSkill`/`allSpecies` individually — the read-only display of those fields happens today inline in the vet lister's row, per `client-shell`.
