# Client Shell

UI patterns every generated screen (Angular, Svelte, or a future stack) must follow, independent of framework. Per-entity capabilities' `## UI Requirements` sections reference this instead of restating it.

## Requirements

### Requirement: Every entity gets a lister, an editor, and a viewer

- **Lister**: a paginated/filterable/sortable table of entities, showing the minimum set of properties needed to identify a row (e.g. name), linking to the viewer/editor.
- **Editor**: a form for creating or updating one entity, showing all properties except its primary key and version.
- **Viewer**: a read-only display of one entity, used when the caller does not have (or does not need) write access.

### Requirement: Lister reflects the REST conventions' filtering and pagination

Filter controls map directly to the query parameters defined in `rest-conventions`; the lister does not invent its own filtering/pagination behavior.

### Requirement: Editor surfaces optimistic-concurrency conflicts to the user

#### Scenario: Another user changed the entity first
- **GIVEN** a user has an editor open on an entity with a given ETag
- **AND** the entity was changed elsewhere in the meantime
- **WHEN** the user submits their edit
- **THEN** the backend responds `412 Precondition Failed`, and the UI shows a conflict message with an option to reload the current data (never silently overwrites, never silently discards the user's input)

### Requirement: Validation errors are shown next to the field they belong to

A `400` response's field-level errors (per `rest-conventions`) are mapped to the corresponding form field, not shown only as a generic banner.

### Requirement: Loading and empty states are explicit

Every lister/editor/viewer distinguishes "loading," "loaded with zero results," and "loaded with data" — never shows a blank screen while a request is in flight or after an empty result.

### Requirement: Errors surface as a consistent toast/banner pattern

A non-2xx response the UI cannot resolve locally (e.g. a 5xx, a network failure) is shown via one consistent error-presentation pattern across every screen, not handled ad hoc per component.

### Requirement: Auth/session handling (planned, not yet active)

Once the future security plan lands, this requirement defines: where the token is stored, how it's attached to requests, and what happens on a `401` (redirect to a login flow). Until then, no client performs any auth/session handling, since no endpoint requires it (`security` capability).

### Requirement: Editor renders one labeled field per property, in a fixed order, ending in a fixed Ok/Cancel action pair

Each field is wrapped in its own labeled group whose visible title *is* the field's name; the same text is exposed as the field's accessible name. A field's control type follows its data: a short piece of text or a number is a plain text/number input, a date or time is a native date/time picker, a longer piece of text is a multi-line text area, a fixed one-of-many choice is a dropdown with a non-selectable "choose a …" placeholder option, a fixed many-of-many choice is a multi-select listing every option at once, and a reference to another entity is a dropdown populated from that entity's item-selection list — never a free-text identifier. The action row always ends with exactly two buttons, in this order: an affirmative submit action labelled "Ok", disabled until the form has been changed from its initial values and every field is valid, and a "Cancel" action that discards the change and restores the field values from before editing.

#### Scenario: Submit stays disabled until the form is both touched and valid
- **GIVEN** an editor freshly opened for an existing entity
- **WHEN** no field has been changed yet
- **THEN** "Ok" is disabled, even though every field already holds a valid value

#### Scenario: Cancel restores the original values
- **GIVEN** an editor open for an existing entity, with one field changed to an invalid or unwanted value
- **WHEN** the user clicks "Cancel"
- **THEN** the field values revert to what they were when the editor opened, and no request is sent to the backend

### Requirement: Lister renders entities in a table, one create action in the header, and a fixed set of row actions in the body

The header row carries one column per displayed property, each with a short title, followed by a trailing cell holding a single circular icon button titled "Add a new <entity>" that opens a create editor for that entity. Each body row's own tooltip identifies the entity it represents (typically its display name), and it identifies itself via a link on its primary display column that navigates to the entity's viewer/detail route. Each row carries its own row of circular icon action buttons, each with a tooltip naming exactly what it does; entity-specific actions (if any) come first, followed by "Delete a(n) <entity>", and finally "Edit a(n) <entity>" last. Creating or editing an entity does not navigate away from the lister: the editor for that operation is inserted as an extra, full-width row directly in the table — at the top for a create, directly below the row being changed for an update — so the rest of the list stays visible around it. While any such editor row is open, every row action and the create action on that lister are disabled. Deleting an entity asks the user to confirm (naming the entity) before the request is sent. When the entity set is empty, the table body shows a single full-width row saying "No <entities>" instead of an empty table.

#### Scenario: Editing a row inserts an editor without navigating away
- **GIVEN** a lister showing several entities
- **WHEN** the user clicks the edit action on one row
- **THEN** an editor for that entity appears as a new row directly below it, the rest of the list remains visible, and every action button on every other row becomes disabled until the editor closes

#### Scenario: An empty result set is shown explicitly
- **GIVEN** a lister whose current filter matches no entities
- **WHEN** the list finishes loading
- **THEN** the table shows one row stating there are no entities, not an empty table body

#### Scenario: Deleting asks for confirmation first
- **GIVEN** a lister row
- **WHEN** the user clicks that row's delete action
- **THEN** the user must confirm (shown a hint naming the entity) before any request reaches the backend

### Requirement: A free-text lister filter is a criteria box paired with a search button titled "Filter items"

Where a lister's filter is free text rather than a required selection, it is one criteria box plus a submit button tooltipped "Filter items"; both are disabled while a create/update editor on that lister is open. An entity-specific capability may instead require a selection (e.g. from another entity's item-selection list) before it will list anything — see that capability's UI Requirements for the exception.

### Requirement: A fixed-vocabulary field is populated from the shared enum picklist for its category

Where a property's set of valid values is centrally managed rather than fixed in code (e.g. a species or a skill), its dropdown/multi-select is populated from that category's picklist, per `enum-management`, instead of being hard-coded per screen.
