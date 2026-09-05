# Vet Management

Manage veterinarians and the skills/species they handle. References `rest-conventions`, `graphql-conventions`, `client-shell`, and `security` instead of restating their rules.

## REST Requirements

### Requirement: Vet exposes name, skills, and species

A `Vet` has `name` (string) and two collections: `allSkill` and `allSpecies` (each a sorted set of non-blank strings; uniqueness within each set is enforced at the application layer).

### Requirement: Full CRUD at `/api/vet`

`POST /api/vet`, `GET /api/vet`, `GET /api/vet/{id}`, `PUT /api/vet/{id}`, `PATCH /api/vet/{id}`, `DELETE /api/vet/{id}` behave per `rest-conventions`.

### Requirement: Authorization

No endpoint under `/api/vet` currently requires authentication — see `security`. (The future security plan, `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`, is expected to give this capability the tightest read/write restriction of the four, since it's clinic-internal data — not decided yet.)

## GraphQL Requirements

### Requirement: `allVet`, `vetById` queries

Per `graphql-conventions`.

### Requirement: Authorization

`/graphql` queries touching `Vet` currently require no authentication — see `security`.

## UI Requirements

### Requirement: Vet lister, editor, viewer

Per `client-shell`. Lister identifies rows by `name`. Editor edits `name` and lets the user add/remove entries in `allSkill` and `allSpecies` as tag-style multi-value inputs, not a single free-text field.
