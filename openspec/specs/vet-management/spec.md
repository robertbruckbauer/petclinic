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

Per `client-shell` and `client-style`. Lister identifies rows by `name`. Editor edits `name` and lets the user add/remove entries in `allSkill` and `allSpecies` as tag-style multi-value inputs, not a single free-text field.

### Requirement: Vet editor presents Name, Skills, and Species, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `name` | Name | single-line text |
| 2 | `allSkill` | Skills | multi-select, all options shown at once, populated from the `skill` picklist (`enum-management`) |
| 3 | `allSpecies` | Species | multi-select, all options shown at once, populated from the `species` picklist (`enum-management`) |

All three are required.

### Requirement: Vet lister shows Name and Skills columns, plus per-row edit/delete actions

| Order | Column | Title |
|---|---|---|
| 1 | `name` | Name |
| 2 | `allSkill` | Skills |

A vet with no skills shows "No skills" instead of an empty cell. Each row additionally carries, in order: "Delete a vet", "Edit a vet". The header's trailing action is titled "Add a new vet".

### Requirement: Vet viewer currently shows only a minimal identification, not the full field set

Unlike the editor, the dedicated vet viewer route does not yet render `name`/`allSkill`/`allSpecies` individually — the read-only display of those fields happens today inline in the vet lister's row, per `client-shell`.
