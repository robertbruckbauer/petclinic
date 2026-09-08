# Enum Management

Manage the shared picklists (e.g. species, skill) that other capabilities' fixed-vocabulary fields are populated from. References `client-shell` instead of restating its rules. Backed by one generic entity discriminated by an `art` (picklist category), not a capability-specific one per category.

## REST Requirements

### Requirement: An Enum item exposes its picklist category, a numeric code, a name, and a text

An `Enum` item has `art` (string, the picklist category, e.g. `species`, `skill`), `code` (non-negative integer), `name` (string), and `text` (string, a longer description). `code` and `name` are each unique within their `art`.

### Requirement: CRUD at `/api/enum/{art}`

`POST /api/enum/{art}`, `GET /api/enum/{art}`, `PUT /api/enum/{art}/{code}`, `DELETE /api/enum/{art}/{code}` manage the items of one picklist category. Unlike `rest-conventions`, there is no per-item `GET`, no `PATCH`, and no `If-Match`/`ETag` concurrency control — `PUT` always replaces `name`/`text` unconditionally, and neither `code` nor `art` can be changed once an item is created.

### Requirement: Authorization

No endpoint under `/api/enum` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allEnum(art)` query

Returns every item of one picklist category. There is no per-item query equivalent to the other capabilities' `<entity>ById`.

### Requirement: Authorization

`/graphql` queries touching `Enum` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Enum lister and editor; no separate viewer

Per `client-shell`, except there is no dedicated viewer: a picklist category is administered entirely through its lister and editor, addressed at a route named after its category — the Clinic menu's "Skill" and "Species" entries navigate to the `skill` and `species` categories of this same generic screen.

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
