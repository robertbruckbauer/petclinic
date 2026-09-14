# Backend API Conventions

Cross-cutting contract every REST endpoint and every GraphQL operation in this system follows. Per-entity capabilities (`backend-api-owner`, `backend-api-pet`, `backend-api-vet`, `backend-api-visit`, `backend-api-ping`, `backend-api-enum`) reference this instead of restating it — `backend-api-enum` and `backend-api-ping` each deviate from several of the REST rules explicitly rather than following them. GraphQL is currently backend-only — no shipped client consumes it yet (both `client-angular` and `client-svelte` are REST-only).

## REST Requirements

### Requirement: Commands and queries are distinguished by HTTP method

`POST`, `PUT`, `PATCH`, `DELETE` are commands (they change state). `GET` is the only query method.

#### Scenario: Reading data never has side effects
- **GIVEN** any `GET` request to an `/api/**` endpoint
- **WHEN** the request is processed
- **THEN** no persisted state changes as a result

### Requirement: PATCH uses JSON Merge Patch semantics

A `PATCH` request body contains only the fields being changed — never the full entity.

#### Scenario: Partial update touches only the given fields
- **GIVEN** an existing entity with several properties
- **WHEN** a `PATCH` request is sent with a JSON body containing one property
- **THEN** only that property is updated; every other property (including collections and relations not mentioned) is unchanged

#### Scenario: A collection or relation update replaces its value, not merges it
- **GIVEN** an entity with a collection property
- **WHEN** a `PATCH` request includes that collection with a new set of values
- **THEN** the collection is replaced wholesale with the given values (JSON Merge Patch semantics, not element-wise merge)

### Requirement: Optimistic concurrency uses ETag / If-Match

Every entity response carries an `ETag` header derived from its `version`. Every mutating request to a specific entity (`PUT`, `PATCH`, `DELETE`) requires `If-Match`.

#### Scenario: Stale write is rejected
- **GIVEN** a client holds an entity's ETag from an earlier `GET`
- **AND** the entity has since been modified by another request
- **WHEN** the client sends `PUT`/`PATCH`/`DELETE` with the stale `If-Match` value
- **THEN** the server responds `412 Precondition Failed` and does not apply the change

#### Scenario: Conditional GET avoids re-transferring unchanged data
- **GIVEN** a client holds an entity's ETag
- **WHEN** it sends `GET` with `If-None-Match` matching the current ETag
- **THEN** the server responds `304 Not Modified`

### Requirement: Request/response semantics use consistent status codes and content types

All request/response bodies are `application/json` (or `application/hal+json` for collection responses). Status codes are used consistently across all entities:

| Status | Meaning |
|---|---|
| 200 | Read or update succeeded |
| 201 | Create succeeded (`Location` header set) |
| 400 | Validation failed |
| 404 | Entity not found |
| 409 | Unique constraint would be violated |
| 412 | ETag mismatch (stale write) |

### Requirement: Collection endpoints support filtering by query parameter

Filterable fields accept case-insensitive matching via query parameters named after the field: a plain value matches as a substring (e.g. `?name=Max`), while a value containing `%` is matched as a full SQL-style `LIKE` pattern (e.g. `?name=Max%` for prefix, `?name=%Max` for suffix). A date or numeric field instead matches exactly for one value, as a range for two values, or as a set for three or more.

#### Scenario: Filtering is case-insensitive
- **GIVEN** an entity with a string field
- **WHEN** a `GET` request supplies that field as a query parameter with different casing than the stored value
- **THEN** matching entities are still returned

### Requirement: Collection endpoints support pagination and sorting

Collection responses use `page`/`size`/`sort` query parameters and return a HAL-style page envelope (`_embedded`, `page: {size, totalElements, totalPages, number}`).

#### Scenario: Default page size is applied when not specified
- **GIVEN** a `GET` request to a collection endpoint without `page`/`size`
- **WHEN** the request is processed
- **THEN** the response uses the endpoint's default page size and returns page 0

### Requirement: Item-selection endpoints are unpaginated and pre-sorted

A `/search/findAllItem`-style endpoint returns the full filtered result set, always sorted by the entity's natural display name, never paginated.

### Requirement: A relation is written by reference but read back as an embedded item, not the full entity

A to-one or to-many relation field is write-only on its owning entity: a mutating request sets it via the related entity's URI (or a set of URIs, for a to-many relation), but the entity's own JSON never echoes that field back. Instead, every response carries one derived field per relation instead — `<relation>Item` for a to-one relation, `all<Entity>Item` for a to-many collection — each holding the related entity's id and display text (the same `{value, text}` shape as an item-selection endpoint's result), embedded directly in the parent. The full related entity is fetched separately: by id via its own endpoint, or via a dedicated relation sub-resource where one exists.

#### Scenario: A to-one relation is set by reference and read back as an item
- **GIVEN** an entity with a to-one relation
- **WHEN** it is created or updated with that relation set to another entity's URI
- **THEN** a subsequent `GET` never returns the relation field itself, but does return a `<relation>Item` field carrying that related entity's id and display text

### Requirement: Validation failures return field-level errors

A `400 Bad Request` response body identifies which field(s) failed and why, not just a generic message.

### Requirement: Errors use a consistent shape and error code mapping

Every error response (400/404/409/412) is produced by a single `RestControllerAdvice` per module and uses the same JSON error shape across all entities.

#### Scenario: Same constraint violation produces the same status on every entity
- **GIVEN** two different entities that both enforce a unique constraint
- **WHEN** a request on either violates that constraint
- **THEN** both respond `409 Conflict` with the same error body shape

### Requirement: Authorization requirements are declared per endpoint

Every REST endpoint's spec (in its per-entity capability) states its current authorization requirement explicitly, even when that requirement is "none" — see the `backend-security` capability for the current baseline.

## GraphQL Requirements

### Requirement: Only queries are exposed today, no mutations

The schema exposes read operations — `all{Entity}`, a filtered variant of it (e.g. `all{Entity}By{Criteria}`, or an `all{Entity}` that takes an argument), `{entity}ById`, `{entity}ByName`, and relation fields — and no mutation type. Writes happen through REST.

### Requirement: Collection queries return an empty list, not an error, when nothing matches

#### Scenario: No entities exist
- **GIVEN** no entities of a given type are persisted
- **WHEN** the corresponding `all{Entity}` query runs
- **THEN** it returns `[]`, not `null` and not an error

### Requirement: Object queries return null for a missing identifier, not an error

#### Scenario: Query by unknown id
- **GIVEN** an id that does not correspond to any persisted entity
- **WHEN** `{entity}ById(id: ...)` runs
- **THEN** the query returns `null`

### Requirement: Relations are resolved with batch loading

A field that navigates a one-to-many or many-to-one relation (e.g. an owner's pets) is resolved via `@BatchMapping` so that querying a collection plus its relations issues a bounded number of queries, not one per parent.

### Requirement: Validation errors are reported as GraphQL errors with a classification extension

A malformed query or an argument that fails validation produces a GraphQL `errors[]` entry with `extensions.classification` set to a consistent value (e.g. `ValidationError`), not a partial/ambiguous `data` payload.

### Requirement: Authorization requirements are declared per operation

Every GraphQL query's spec (in its per-entity capability) states its current authorization requirement explicitly — see the `backend-security` capability for the current baseline.
