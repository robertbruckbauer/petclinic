# Client UI: Vet

The UI for veterinarians and the skills/species they handle. References `client-shell`, `client-style`, and `backend-api-vet` instead of restating their rules.

## UI Requirements

### Requirement: Vet lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `name`. Editor edits `name` and lets the user add/remove entries in `allSkill` and `allSpecies` as tag-style multi-value inputs, not a single free-text field.

### Requirement: Vet editor presents Name, Skills, and Species, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `name` | Name | single-line text |
| 2 | `allSkill` | Skills | multi-select, all options shown at once, populated from the `skill` picklist (`backend-api-enum`) |
| 3 | `allSpecies` | Species | multi-select, all options shown at once, populated from the `species` picklist (`backend-api-enum`) |

All three are required.

### Requirement: Vet lister shows Name and Skills columns, plus per-row edit/delete actions

| Order | Column | Title |
|---|---|---|
| 1 | `name` | Name |
| 2 | `allSkill` | Skills |

A vet with no skills shows "No skills" instead of an empty cell. Each row additionally carries, in order: "Delete a vet", "Edit a vet". The header's trailing action is titled "Add a new vet".

### Requirement: Vet viewer currently shows only a minimal identification, not the full field set

Unlike the editor, the dedicated vet viewer route does not yet render `name`/`allSkill`/`allSpecies` individually — the read-only display of those fields happens today inline in the vet lister's row, per `client-shell`.
