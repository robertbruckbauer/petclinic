# Backend API: Enum

The REST and GraphQL API for the shared picklists (e.g. species, skill) that other capabilities' fixed-vocabulary fields are populated from. References `backend-api-conventions` instead of restating its rules — though it deviates from several of its REST rules explicitly, as noted below. Backed by one generic entity discriminated by an `art` (picklist category), not a capability-specific one per category. See `client-ui-enum` for the UI built on this API.

## REST Requirements

### Requirement: Enum model

An `Enum` item has `art` (string, the picklist category, e.g. `species`, `skill` — set once at creation, never changed), `code` (non-negative integer, unique within its `art`, set once at creation, never changed), `name` (string, unique within its `art`), and `text` (string, a longer description). Like `Ping`, but unlike `Owner`/`Pet`/`Vet`/`Visit`, there is no `If-Match`/`ETag` concurrency control on `Enum` at all.

### Requirement: `POST /api/enum/{art}` creates a picklist item

Creates a new `Enum` item in the given `art` category from `code`, `name`, and `text`.

Reports `201 Created` on success, `400 Bad Request` if validation fails, `409 Conflict` if `code` or `name` is already taken within that `art`.

### Requirement: `GET /api/enum/{art}` lists a picklist category's items

Returns every `Enum` item for the given `art`, ordered by `code`. Unlike every other capability's collection endpoint, this does not accept filter, sort, or pagination query parameters, and always returns the full result set — it can be empty if the category has no items yet.

Reports `200 OK`.

### Requirement: `PUT /api/enum/{art}/{code}` updates a picklist item

Replaces an existing item's `name` and `text`. Unlike every other capability's `PUT`, this only updates an existing item — it never creates one, and `art`/`code` cannot be changed by it.

Reports `200 OK` if updated, `404 Not Found` if no item exists at that `art`/`code`, `400 Bad Request` if validation fails, `409 Conflict` if `name` is already taken by another item in the same `art`.

### Requirement: `DELETE /api/enum/{art}/{code}` deletes a picklist item

Deletes the item at the given `art`/`code`.

Reports `200 OK` with the deleted item if it existed, `404 Not Found` otherwise.

### Requirement: Authorization

No endpoint under `/api/enum` currently requires authentication.

## GraphQL Requirements

### Requirement: `allEnum` query

Returns every `Enum` item for the given `art`, ordered by `code`. Returns `[]`, not an error, if the category has no items. This is the only `Enum` query — there is no per-item query, and no mutation type; create/update/delete are REST-only.

### Requirement: `code` risks overflow in the schema

`code` is typed `Int!` in the schema — a signed 32-bit integer — while the underlying value is a 64-bit `Long`. A `code` beyond 2,147,483,647 would fail to serialize.

### Requirement: Authorization

`/graphql` queries touching `Enum` currently require no authentication.
