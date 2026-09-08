# Owner Management

Manage pet owners acting as clients of the clinic. References `rest-conventions`, `graphql-conventions`, `client-shell`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Owner exposes name, address, and contact

An `Owner` has `name` (string), `address` (string), `contact` (string), and a read-only collection `allPet` of the owner's pets.

### Requirement: Full CRUD at `/api/owner`

`POST /api/owner`, `GET /api/owner`, `GET /api/owner/{id}`, `PUT /api/owner/{id}`, `PATCH /api/owner/{id}`, `DELETE /api/owner/{id}` behave per `rest-conventions`.

### Requirement: `name` is filterable, and item selection is available

`GET /api/owner` supports `?name=`, `?address=`, `?contact=` filters per `rest-conventions`. `GET /api/owner/search/findAllItem` returns simplified `OwnerItem`s sorted by name, per `rest-conventions`' item-selection rule.

### Requirement: Authorization

No endpoint under `/api/owner` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allOwner`, `ownerById`, `ownerByName` queries

Per `graphql-conventions`. The `allPet` relation is resolved via batch loading.

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
