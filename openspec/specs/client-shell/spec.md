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

### Requirement (planned, not yet active): Auth/session handling

Once the future security plan lands, this requirement defines: where the token is stored, how it's attached to requests, and what happens on a `401` (redirect to a login flow). Until then, no client performs any auth/session handling, since no endpoint requires it (`security` capability).
