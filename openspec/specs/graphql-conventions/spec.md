# GraphQL Conventions

Cross-cutting contract every GraphQL operation in this system follows. Per-entity capabilities reference this instead of restating it. GraphQL is currently backend-only — no shipped client consumes it yet (both `client-angular` and `client-svelte` are REST-only).

## Requirements

### Requirement: Only queries are exposed today, no mutations

The schema exposes read operations (`all{Entity}`, `{entity}ById`, `{entity}ByName`, and relation fields) and no mutation type. Writes happen through REST.

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

Every GraphQL query's spec (in its per-entity capability) states its current authorization requirement explicitly — see the `security` capability for the current baseline.
