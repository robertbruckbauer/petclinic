---
category: Frontend
related:
  - rxjs-service-pattern.md
  - ../../openspec/specs/client-shell/spec.md
status: current
updated: 2026-09-05
---

# Lister/editor/viewer component pattern

Both current clients (Angular, Svelte) structure every entity's UI as three components:

- **Lister** — a filterable/paginated table, identifying rows by the minimum set of properties needed (e.g. `name`), linking into the editor/viewer.
- **Editor** — a form for creating/updating one entity; never shows or edits the primary key or version.
- **Viewer** — a read-only display of one entity.

This is the framework-independent shape that `openspec/specs/client-shell/spec.md` normalizes as a requirement, so it can be regenerated identically regardless of which framework a client uses. Framework-specific implementation baselines: `doc/concept/angular/entity-lister.adoc` / `entity-editor.adoc` and `doc/concept/svelte/entity-lister.adoc` / `entity-editor.adoc`.
