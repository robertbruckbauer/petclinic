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

Per `client-shell`. Lister identifies rows by `name`. Editor edits `name`, `address`, `contact` (not `id`/`version`). A pet's owner is navigable from the pet screens, not re-entered as free text.
