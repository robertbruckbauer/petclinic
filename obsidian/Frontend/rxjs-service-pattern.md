---
category: Frontend
related:
  - lister-editor-viewer-pattern.md
  - etag-optimistic-concurrency-ui.md
status: current
updated: 2026-09-05
---

# RxJs service pattern

Both current clients use a per-entity service class (`{entity}.service.ts`) wrapping REST calls in RxJs `Observable`s — this is the one place HTTP/REST detail lives; lister/editor/viewer components consume the service, they never call `fetch`/HTTP directly. This keeps the REST contract (`openspec/specs/rest-conventions/spec.md`) implemented in exactly one place per entity, and is why the two frameworks (Angular, Svelte) can share the same conceptual service layer despite different component models.

Implementation baseline: `doc/concept/angular/entity-service.adoc` / `doc/concept/svelte/entity-service.adoc`.
