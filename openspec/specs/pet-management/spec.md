# Pet Management

Manage pets belonging to an owner. References `rest-conventions`, `graphql-conventions`, `client-shell`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Pet exposes name, birth date, species, sex, and its owner

A `Pet` has `name` (string), `born` (date), `species` (string), `sex` (enum), and a required relation to exactly one `owner`.

### Requirement: Full CRUD at `/api/pet`

`POST /api/pet`, `GET /api/pet`, `GET /api/pet/{id}`, `PUT /api/pet/{id}`, `PATCH /api/pet/{id}`, `DELETE /api/pet/{id}` behave per `rest-conventions`.

### Requirement: A pet cannot exist without a valid owner reference

#### Scenario: Creating a pet with an unknown owner
- **GIVEN** a `POST /api/pet` request referencing an owner id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request` (per `rest-conventions`' validation rule)

### Requirement: Authorization

No endpoint under `/api/pet` currently requires authentication — see `security`.

## GraphQL Requirements

### Requirement: `allPet`, `petById` queries

Per `graphql-conventions`. The `owner` relation is resolved via batch loading.

### Requirement: Authorization

`/graphql` queries touching `Pet` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Pet lister, editor, viewer

Per `client-shell`. Lister identifies rows by `name` (scoped to the current owner when navigated from the owner screens). Editor edits `name`, `born`, `species`, `sex`, and lets the user pick an `owner` from an item-selection list (`owner-management`'s `findAllItem` endpoint), never a free-text owner id.
