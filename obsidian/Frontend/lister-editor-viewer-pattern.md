---
category: Frontend
related:
  - rxjs-service-pattern.md
  - ../../openspec/specs/client-shell/spec.md
  - ../../openspec/specs/client-style/spec.md
status: current
updated: 2026-09-08
---

# Lister/editor/viewer component pattern

Both current clients (Angular, Svelte) structure every entity's UI as three components:

- **Lister** — a filterable/paginated table, identifying rows by the minimum set of properties needed (e.g. `name`), linking into the editor/viewer.
- **Editor** — a form for creating/updating one entity; never shows or edits the primary key or version.
- **Viewer** — a read-only display of one entity.

This is the framework-independent shape that `openspec/specs/client-shell/spec.md` normalizes (the inventory + behavioral contract) and `openspec/specs/client-style/spec.md` normalizes (each one's concrete field/column layout and appearance), so it can be regenerated identically regardless of which framework a client uses. Framework-specific implementation baselines: `doc/concept/angular/entity-lister.adoc` / `entity-editor.adoc` and `doc/concept/svelte/entity-lister.adoc` / `entity-editor.adoc`.

**Pitfall:** the third component is not uniformly implemented. Only `visit`'s viewer actually renders its entity's fields read-only; `owner`/`pet`/`vet`'s dedicated viewer routes are currently a bare placeholder heading with no fields at all — the read-only display of those three entities happens today inline in their lister's row instead (per each capability's own "viewer currently shows only a minimal identification" requirement). Don't assume a viewer route has real content without checking that capability's spec.
