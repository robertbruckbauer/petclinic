# Client UI: Enum

The UI for administering the shared picklists (e.g. species, skill) that other capabilities' fixed-vocabulary fields are populated from. References `client-shell`, `client-style`, and `backend-api-enum` instead of restating their rules.

## UI Requirements

### Requirement: Enum lister and editor; no separate viewer

Per `client-shell` and `client-style`, except there is no dedicated viewer: a picklist category is administered entirely through its lister and editor, addressed at a route named after its category — the Clinic menu's "Skill" and "Species" entries navigate to the `skill` and `species` categories of this same generic screen.

### Requirement: Enum editor presents Code, Name, and Text, in that order

| Order | Field | Title | Type |
|---|---|---|---|
| 1 | `code` | Code | number |
| 2 | `name` | Name | single-line text |
| 3 | `text` | Text | multi-line text |

All three are required. `code` is editable when creating an item, but read-only once the item exists — only `name`/`text` can be changed on update.

#### Scenario: Code cannot be changed once an item exists
- **GIVEN** the editor open to update an existing picklist item
- **WHEN** the form renders
- **THEN** the Code field is read-only, while Name and Text remain editable

### Requirement: Enum lister shows Code, Name, and Text columns

| Order | Column | Title |
|---|---|---|
| 1 | `code` | Code |
| 2 | `name` | Name |
| 3 | `text` | Text (hidden on narrow screens) |

Each row additionally carries, in order: "Delete an item", "Edit an item". The header's trailing action is titled "Add a new item". The filter box matches items whose `name` or `text` starts with the given text.
