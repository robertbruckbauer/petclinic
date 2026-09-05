# Visit Management

Manage visits where a vet examines a pet. References `rest-conventions`, `graphql-conventions`, `client-shell`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Visit exposes date, time, notes, billing info, duration, and its pet/vet

A `Visit` has `date` (date), `time` (time), `text` (notes, string), `billable` (boolean), `duration`, and required relations to exactly one `pet` and one `vet`.

### Requirement: Full CRUD at `/api/visit`

`POST /api/visit`, `GET /api/visit`, `GET /api/visit/{id}`, `PUT /api/visit/{id}`, `PATCH /api/visit/{id}`, `DELETE /api/visit/{id}` behave per `rest-conventions`.

### Requirement: A visit cannot exist without a valid pet and vet reference

#### Scenario: Creating a visit with an unknown pet or vet
- **GIVEN** a `POST /api/visit` request referencing a `pet` or `vet` id that does not exist
- **WHEN** the request is processed
- **THEN** the server responds `400 Bad Request` (per `rest-conventions`' validation rule)

### Requirement: Authorization

No endpoint under `/api/visit` currently requires authentication — see `security`. (Whether a vet may only edit visits assigned to them is an open question for the future security plan — see `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`.)

## GraphQL Requirements

### Requirement: `allVisit`, `visitById` queries

Per `graphql-conventions`. The `pet` and `vet` relations are resolved via batch loading.

### Requirement: Authorization

`/graphql` queries touching `Visit` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Visit lister, editor, viewer

Per `client-shell`. Lister identifies rows by `date` + `pet` name (scoped to the current pet or vet when navigated from those screens). Editor edits `date`, `time`, `text`, `billable`, `duration`, and lets the user pick `pet` and `vet` from item-selection lists, never free-text ids.
