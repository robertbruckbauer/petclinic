# Backend API: Pet

The REST and GraphQL API for pets belonging to an owner. References `backend-api-conventions`, `backend-api-owner`, and `backend-security` instead of restating their rules. See `client-ui-pet` for the UI built on this API.

## REST Requirements

### Requirement: Pet model

A `Pet` has `name` (string, required, unique per owner — not globally), `born` (date, required), `species` (string, required), `sex` (enum `M`/`F`, required), and its owner — a required relation, write-only and read back as `ownerItem`, per `backend-api-conventions`' relation-item rule.

### Requirement: `POST /api/pet` creates a pet

Creates a new `Pet` from `name`, `born`, `species`, `sex`, and a reference to its `owner`.

Reports `201 Created` on success, `400 Bad Request` if validation fails or `owner` references an entity that does not exist, `409 Conflict` if `name` is already taken by another pet of the same owner.

#### Scenario: Creating a pet with an unknown owner
- **GIVEN** a `POST /api/pet` request referencing an `owner` id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request`

### Requirement: `GET /api/pet` lists pets

Returns a paginated, sortable, filterable collection of `Pet`, per `backend-api-conventions`. `name`, `species`, and `owner.name` are each filterable as a case-insensitive substring or `LIKE` pattern; `sex` matches its exact code (`M` or `F`).

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

Updates only the given fields of an existing `Pet`, including reassigning `owner` alone, per `backend-api-conventions`' JSON Merge Patch rule.

Reports `200 OK` if updated, `404 Not Found` if the pet does not exist, `400`/`409`/`412` as for `PUT`.

### Requirement: `DELETE /api/pet/{id}` deletes a pet

Deletes the `Pet` with the given id.

Reports `200 OK` with the deleted entity if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/pet` currently requires authentication — see `backend-security`.

## GraphQL Requirements

### Requirement: `allPet` query

Returns every `Pet`. Returns `[]`, not an error, if none exist.

### Requirement: `petById` query

Returns the `Pet` with the given `id`, or `null` if none exists.

### Requirement: `owner` relation is batch-loaded

A pet's `owner` field is resolved via batch loading (per `backend-api-conventions`) rather than one query per pet.

### Requirement: Authorization

`/graphql` queries touching `Pet` currently require no authentication — see `backend-security`.
