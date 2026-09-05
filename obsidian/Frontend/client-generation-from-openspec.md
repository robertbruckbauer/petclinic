---
category: Frontend
related:
  - ../../doc/arc42/adr/0002-openspec-driven-client-generation.adoc
  - lister-editor-viewer-pattern.md
status: current
updated: 2026-09-05
---

# Generating a client from OpenSpec

A standalone client repository (`petclinic-angular-ui`, `petclinic-svelte-ui`) is meant to be regenerated fully from its synced `openspec/` subset, not incrementally patched forever. Practically, that means:

- Every screen traces to a `## UI Requirements` entry in a per-entity capability spec (`owner-management`, `pet-management`, `vet-management`, `visit-management`) plus the shared `client-shell` capability.
- Every service call traces to a `## REST Requirements` (or, once adopted, `## GraphQL Requirements`) entry in the same spec.
- Framework-specific patterns (this category's other notes) fill the gap between "what must be true" (OpenSpec) and "how it looks in Angular/Svelte" — an AI regenerating a client reads both, in that order.

See `doc/arc42/adr/0002-openspec-driven-client-generation.adoc` for why clients live in separate repositories at all.
