# Client Style

Every generated screen's concrete form/table composition (field order & type, table columns & row actions, filter widgets, picklist sourcing) and its visual appearance (color, iconography, control shape, typography, responsive breakpoint, motion) — independent of framework. Supplements `client-shell`, which defines only the high-level screen inventory (a lister, an editor, a viewer per entity) and the behavioral contract driven by backend calls and security. Per-entity capabilities' `## UI Requirements` sections reference this instead of restating it, the same way they reference `client-shell`.

## Requirements

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

### Requirement: Every screen renders through one shared visual theme

Angular and Svelte apply the exact same color, shape, spacing, and type system — neither renders its own one-off colors, fonts, or component shapes. Every rule from here on is a role ("the alert color," "a circular icon button"), not a specific value, so a future stack can satisfy it with its own design-system implementation as long as the role is honored consistently everywhere it applies.

### Requirement: A small, neutral color palette carries meaning by role, not by decoration

- The page and its chrome use a plain neutral background; no screen or section is tinted to give it an identity.
- A table's header row sits on a light neutral fill with muted (lower-contrast) header text.
- Alternating body rows, and a row that has an inline create/update editor open beneath it, use a slightly darker neutral fill than a plain row — purely to aid scanning, not to signal state.
- A row that is selected, or a row whose editor is currently open, is marked by an accent-colored vertical bar on its left edge (thin for "selected," thicker for "editor open here") — never by recoloring the row itself.
- Exactly one alert color exists in the whole system, and it is reserved for the error/notification toast (`client-shell`'s "errors surface as a consistent toast/banner" requirement); nothing else uses it.
- A hyperlink is always both underlined and rendered in the single accent color used for links everywhere, so it is identifiable without relying on color alone.

#### Scenario: Only the toast uses the alert color
- **GIVEN** any screen in either client
- **WHEN** nothing has gone wrong
- **THEN** the alert color does not appear anywhere on screen — it is introduced only when a toast is shown

### Requirement: One icon glyph set represents every action, and an icon never stands alone

Every icon-only control draws from a single, consistent glyph set at one fixed size across the entire app. The same glyph always means the same action wherever it recurs (a magnifier for filter/search, a plus for create, a pencil for edit, a trash can for delete, a list glyph for "show all," and so on for the remaining per-entity actions) so a returning user recognizes the action by shape alone. Per this capability's lister row-action requirement above, every one of these icons is additionally paired with a text tooltip and the same text is exposed as its accessible name — the icon alone is never the only description of what the control does.

### Requirement: A control's shape signals its role — circular for icon-only actions, joined rectangles for a form's actions, a block for a button-styled link

- Every icon-only action (a lister's row actions, its header create action, a filter's submit action) renders as a circular, outlined button.
- An editor's two closing actions render as two rectangular buttons visually joined into a single connected group, in the fixed order already defined above (submit, then Cancel).
- A navigational action that is styled as a button rather than a plain link (e.g. a card's "go to the full detail page" action) renders as a plain rectangular block button — visually distinct from both of the above, so its shape alone tells the user it navigates rather than submits or acts on a row.

### Requirement: A six-level heading scale gives every screen the same typographic rhythm

Headings from the most to least prominent level decrease in size and tighten in letter-spacing step by step; every level carries the same vertical spacing above/below and the same horizontal inset wherever it appears, so a heading's level is recognizable from its type style alone, independent of which screen it's on.

### Requirement: Layout reflows from stacked to side-by-side at one shared breakpoint

Below one shared width breakpoint, an editor's fields and a lister row's action icons stack in a single column; at and above that same breakpoint, they lay out side by side (a row, or a multi-column grid for a longer run of icons). The breakpoint is the same value everywhere this reflow happens — no screen defines its own. A column judged secondary on a narrow screen (e.g. `enum-management`'s Text column) is hidden below that breakpoint and reappears above it, rather than being squeezed to fit.

### Requirement: A single shared spinner represents the loading state everywhere

Per `client-shell`'s "loading and empty states are explicit" requirement: every lister/viewer's loading state is the same spinner glyph at the same size, centered at the top of the content area, entirely replacing the list/detail body until data arrives — never a per-screen bespoke loading indicator.

### Requirement: Toast notifications share one position, motion, and lifecycle

Every toast stacks in the same fixed screen corner, slides in on entry, and carries a depleting progress bar along one edge showing time remaining before it auto-dismisses. Hovering a toast pauses its countdown until the pointer leaves. A toast can also be dismissed early via a close glyph on it, or via the keyboard.

#### Scenario: Hovering a toast pauses its countdown
- **GIVEN** a toast is visible and counting down to auto-dismiss
- **WHEN** the user's pointer enters the toast
- **THEN** the countdown (and its progress bar) stops advancing until the pointer leaves, at which point it resumes from where it left off

### Requirement: A disabled control looks inert, not merely behaves inert

Wherever a control is required to be disabled — by one of this capability's own requirements above (e.g. a row action while an editor is open) or by a per-entity capability's (e.g. a create action before a required selection is made) — it is also rendered visibly muted compared to its enabled state; disabling is never signaled by behavior alone with no visual cue.

### Requirement: The app shell is a fixed header and footer around a scrollable content area, with entities grouped under category headings in a collapsible menu

A fixed bar stays pinned to the top of the viewport across every screen, and a second fixed bar stays pinned to the bottom; the content area between them scrolls independently. The top bar's navigation is a collapsible menu (opened/closed by one toggle whose icon itself changes between an "open" and a "close" glyph to reflect the menu's current state) listing every entity's lister under a short category heading (e.g. "Client" groups Owner and Pet; "Clinic" groups Visit, Vet, and the picklist screens from `enum-management`) — not a flat list of every entity.
