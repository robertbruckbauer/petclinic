# Enum Management

Manage the shared picklists (e.g. species, skill) that other capabilities' fixed-vocabulary fields are populated from. References `rest-conventions`, `graphql-conventions`, `client-shell`, `client-style`, and `security` instead of restating their rules — though it deviates from several of `rest-conventions`' rules explicitly, as noted below. Backed by one generic entity discriminated by an `art` (picklist category), not a capability-specific one per category.

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

No endpoint under `/api/enum` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allEnum` query

Returns every `Enum` item for the given `art`, ordered by `code`. Returns `[]`, not an error, if the category has no items. This is the only `Enum` query — there is no per-item query, and no mutation type; create/update/delete are REST-only.

### Requirement: `code` risks overflow in the schema

`code` is typed `Int!` in the schema — a signed 32-bit integer — while the underlying value is a 64-bit `Long`. A `code` beyond 2,147,483,647 would fail to serialize.

### Requirement: Authorization

`/graphql` queries touching `Enum` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Enum lister and editor; no separate viewer

Per `client-shell` and `client-style`, except there is no dedicated viewer: a picklist category is administered entirely through its lister and editor, addressed at a route named after its category — the Clinic menu's "Skill" and "Species" entries navigate to the `skill` and `species` categories of this same generic screen.

### Requirement: Enum editor presents Code, Name, and Text, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `code` | Code | number |
| 2 | `name` | Name | single-line text |
| 3 | `text` | Text | multi-line text |

All three are required. `code` is editable when creating an item, but read-only once the item exists — only `name`/`text` can be changed on update.

#### Scenario: Code cannot be changed once an item exists
- **GIVEN** the editor open to update an existing picklist item
- **WHEN** the form renders
- **THEN** the Code field is read-only, while Name and Text remain editable

### Requirement: Enum lister shows Code, Name, and Text columns

| Order | Column | Title |
|---|---|---|
| 1 | `code` | Code |
| 2 | `name` | Name |
| 3 | `text` | Text (hidden on narrow screens) |

Each row additionally carries, in order: "Delete an item", "Edit an item". The header's trailing action is titled "Add a new item". The filter box matches items whose `name` or `text` starts with the given text.
