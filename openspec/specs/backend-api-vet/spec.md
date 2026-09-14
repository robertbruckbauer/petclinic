# Backend API: Vet

The REST and GraphQL API for veterinarians and the skills/species they handle. References `backend-api-conventions` and `backend-security` instead of restating their rules. See `client-ui-vet` for the UI built on this API.

## REST Requirements

### Requirement: Vet model

A `Vet` has `name` (string, required, unique) and two collections, `allSkill` and `allSpecies` (each a sorted set of non-blank strings; uniqueness within each set is enforced at the application layer, not by a database constraint).

### Requirement: `POST /api/vet` creates a vet

Creates a new `Vet` from `name`, `allSkill`, and `allSpecies`.

Reports `201 Created` on success, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken.

### Requirement: `GET /api/vet` lists vets

Returns a paginated, sortable, filterable collection of `Vet`, per `backend-api-conventions`. `name` is filterable as a case-insensitive substring or `LIKE` pattern; `allSkill` and `allSpecies` are filterable the same way, matching a vet that has at least one matching entry.

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

Updates only the given fields of an existing `Vet`; updating `allSkill` or `allSpecies` replaces that collection wholesale, per `backend-api-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the vet does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/vet/{id}` deletes a vet

Deletes the `Vet` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/vet` currently requires authentication — see `backend-security`. (The future security plan, `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`, is expected to give this capability the tightest read/write restriction of the four, since it's clinic-internal data — not decided yet.)

## GraphQL Requirements

### Requirement: `allVet` query

Returns every `Vet`. Returns `[]`, not an error, if none exist.

### Requirement: `vetById` query

Returns the `Vet` with the given `id`, or `null` if none exists.

### Requirement: `vetByName` query

Returns the `Vet` with the given `name` (exact match, case-sensitive), or `null` if none exists.

### Requirement: Authorization

`/graphql` queries touching `Vet` currently require no authentication — see `backend-security`.
