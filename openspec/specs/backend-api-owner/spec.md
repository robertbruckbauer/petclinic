# Backend API: Owner

The REST and GraphQL API for pet owners acting as clients of the clinic. References `backend-api-conventions` and `backend-security` instead of restating their rules. See `client-ui-owner` for the UI built on this API.

## REST Requirements

### Requirement: Owner model

An `Owner` has `name` (string, required, unique), `address` (string, required), `contact` (string, required to be present but may be blank), and its pets — exposed read-only as `allPetItem`, never as the raw `allPet` relation, per `backend-api-conventions`' relation-item rule.

### Requirement: `POST /api/owner` creates an owner

Creates a new `Owner` from `name`, `address`, and `contact`.

Reports `201 Created` with the new entity on success, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken.

### Requirement: `GET /api/owner` lists owners

Returns a paginated, sortable, filterable collection of `Owner`, per `backend-api-conventions`. `name`, `address`, and `contact` are each filterable as a case-insensitive substring or `LIKE` pattern.

Reports `200 OK`.

#### Scenario: Filtering by name
- **GIVEN** owners named "Max Mustermann" and "Erika Musterfrau"
- **WHEN** `GET /api/owner?name=Max` is requested
- **THEN** only "Max Mustermann" is returned, regardless of case

### Requirement: `GET /api/owner/{id}` gets one owner

Returns the `Owner` with the given id.

Reports `200 OK` if found, `404 Not Found` otherwise.

### Requirement: `GET /api/owner/search/findAllItem` lists owners as items

Returns every `Owner` matching the same filters as `GET /api/owner`, as `OwnerItem` (`value` = id, `text` = `name`), sorted by name, unpaginated — per `backend-api-conventions`' item-selection rule.

Reports `200 OK`.

### Requirement: `PUT /api/owner/{id}` replaces an owner

Replaces an existing `Owner`'s `name`, `address`, and `contact` wholesale, or creates one at that id if none exists yet.

Reports `200 OK` if updated, `201 Created` if created, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken by another owner, `412 Precondition Failed` on a stale `If-Match` (per `backend-api-conventions`).

### Requirement: `PATCH /api/owner/{id}` partially updates an owner

Updates only the given fields of an existing `Owner`, per `backend-api-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the owner does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/owner/{id}` deletes an owner

Deletes the `Owner` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/owner` currently requires authentication — see `backend-security`.

## GraphQL Requirements

### Requirement: `allOwner` query

Returns every `Owner`. Returns `[]`, not an error, if none exist.

### Requirement: `ownerById` query

Returns the `Owner` with the given `id`, or `null` if none exists.

### Requirement: `ownerByName` query

Returns the `Owner` with the given `name` (exact match, case-sensitive), or `null` if none exists.

### Requirement: `allPet` relation is batch-loaded

An owner's `allPet` field is resolved via batch loading (per `backend-api-conventions`) rather than one query per owner.

### Requirement: `contact` is declared nullable even though it is never actually absent

The schema declares `contact: String` (nullable), but the underlying data always has a value — it defaults to an empty string, never `null`. Treat an empty string, not `null`, as "no contact given."

### Requirement: Authorization

`/graphql` queries touching `Owner` currently require no authentication — see `backend-security`.
