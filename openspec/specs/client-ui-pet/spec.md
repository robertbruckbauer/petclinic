# Client UI: Pet

The UI for pets belonging to an owner. References `client-shell`, `client-style`, `backend-api-pet`, and `backend-api-owner` instead of restating their rules.

## UI Requirements

### Requirement: Pet lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `name` (scoped to the current owner when navigated from the owner screens). Editor edits `name`, `born`, `species`, `sex`, and lets the user pick an `owner` from an item-selection list (`backend-api-owner`'s `findAllItem` endpoint), never a free-text owner id.

### Requirement: Pet editor presents Species, Sex, Name, and Born, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `species` | Species | dropdown, populated from the `species` picklist (`backend-api-enum`); each option's tooltip shows that species' descriptive text |
| 2 | `sex` | Sex | dropdown, fixed choice of `male`/`female` |
| 3 | `name` | Name | single-line text |
| 4 | `born` | Born | date |

All four are required. `owner` is not edited on this form: a pet created from the pet lister is scoped to the owner currently selected in the lister's filter; a pet created from the owner screens is scoped to that owner.

### Requirement: Pet lister requires an owner to be chosen before listing, and shows Species and Name columns

The lister's filter is a required dropdown of owners (`backend-api-owner`'s item-selection list, placeholder "Choose an owner"), not a free-text criteria box — the exception to `client-style`'s default filter convention. No pet is requested until an owner is chosen.

| Order | Column | Title |
|---|---|---|
| 1 | `species` | Species |
| 2 | `name` | Name |

Each row additionally carries, in order: "Add a treatment" (opens a visit-create Treatment form for that pet, per `client-ui-visit`), "Delete a pet", "Edit a pet". The header's trailing action is titled "Add a new pet" and stays disabled until an owner is chosen.

#### Scenario: No pets are loaded before an owner is selected
- **GIVEN** the pet lister has just opened with no owner selected
- **WHEN** the page renders
- **THEN** no pet is shown and "Add a new pet" is disabled until an owner is chosen from the filter

### Requirement: Pet viewer currently shows only a minimal identification, not the full field set

Unlike the editor, the dedicated pet viewer route does not yet render `name`/`born`/`species`/`sex`/`owner` individually — the read-only display of those fields happens today inline in the pet lister's row, per `client-shell`.
