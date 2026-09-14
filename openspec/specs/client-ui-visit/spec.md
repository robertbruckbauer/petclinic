# Client UI: Visit

The UI for visits where a vet examines a pet. References `client-shell`, `client-style`, `backend-api-visit`, `client-ui-pet`, `client-ui-owner`, and `backend-api-vet` instead of restating their rules.

## UI Requirements

### Requirement: Visit lister, editor, viewer

Per `client-shell` and `client-style`. Lister identifies rows by `date` + `pet` name and, unlike the other listers, always shows every visit unscoped — the owner-scoped view is this capability's own overview (below), not the lister. Editor edits `date`, `time`, `text`, and `duration` (across the two forms below) and lets the user pick `pet` and `vet` from item-selection lists, never free-text ids; `billable` is not exposed in either form.

### Requirement: A visit is created via a Treatment form scheduling the pet's visit, then recorded via a separate Diagnose form

Creating a visit (from the pet screens) only asks for when the visit happens; recording what happened is a separate step, done afterwards on the same visit.

**Treatment form** (create; embedded inline under the pet whose treatment is being scheduled, per `client-ui-pet`):

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `date` | Treatment | date |
| 2 | `time` | Time | time |

`date` is required, `time` is optional.

**Diagnose form** (update; embedded inline under a visit row, and on the visit detail page):

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `text` | Diagnose | multi-line text |
| 2 | `time` | Time | time |
| 3 | `duration` | Duration | three linked number fields, titled "Hours"/"Minutes"/"Seconds" |
| 4 | `vet` | Vet | dropdown, populated from `backend-api-vet`'s item-selection list, placeholder "Choose a vet" |

`text` and `vet` are required; `time` and `duration` are optional.

#### Scenario: Duration is entered as separate hour/minute/second fields
- **GIVEN** a visit's Diagnose form
- **WHEN** the user enters values into the Hours, Minutes, and Seconds fields
- **THEN** they are combined into the visit's single `duration` value on submit, and all-zero is treated as "not set"

### Requirement: Visit lister groups rows by date and shows Owner, Pet, and Vet columns

| Order | Column | Title |
|---|---|---|
| 1 | `pet.owner` | Owner |
| 2 | `pet` | Pet |
| 3 | `vet` | Vet |

Rows are grouped under a heading showing their `date` whenever it differs from the previous row's. The Owner and Vet cells link to those entities' viewers; the Pet cell links to that visit's own detail page. Each row additionally carries, in order: "Delete a visit", "Edit visit details" (opens the Diagnose form inline). There is no header create action on this lister — a visit is always created from the pet screens, per `client-ui-pet`.

### Requirement: Visit overview shows a per-visit read-only card, grouped under an owner

Shown inline under an owner's row in the owner lister ("Show all visits", per `client-ui-owner`). Each card presents, in order: `pet` (titled "Pet"), `date` (titled "Treatment"), `vet` (titled "Vet"), `text` (titled "Diagnose"), all read-only, followed by an "Edit" link to that visit's detail page.

### Requirement: Visit detail page (the `viewer` route) shows a read-only summary plus both forms

The route addressed by a visit's id shows `date`, `time`, and `duration` (formatted as "<n> hours <n> minutes <n> seconds", or "-" when unset) read-only, titled "Date"/"Time"/"Duration" in that order, followed by the Treatment form and the Diagnose form (both editable in place, per the requirement above) so the full visit can be corrected from one page. Unlike `client-ui-owner`/`client-ui-pet`/`client-ui-vet`, this viewer is fully implemented, not a stub.
