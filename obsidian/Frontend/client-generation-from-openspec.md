---
category: Frontend
related:
  - ../../doc/arc42/adr/0002-openspec-driven-client-generation.adoc
  - lister-editor-viewer-pattern.md
status: current
updated: 2026-09-08
---

# Generating a client from OpenSpec

A standalone client repository (`petclinic-angular-ui`, `petclinic-svelte-ui`) is meant to be regenerated fully from its synced `openspec/` subset, not incrementally patched forever. Practically, that means:

- Every screen traces to a `## UI Requirements` entry in a per-entity capability spec (`owner-management`, `pet-management`, `vet-management`, `visit-management`, `enum-management`) plus the two shared capabilities, `client-shell` (inventory/behavior) and `client-style` (composition/appearance).
- Every service call traces to a `## REST Requirements` entry in the same spec. Each entity capability also has a `## GraphQL Requirements` section describing a real, implemented backend query set — but no shipped client generates GraphQL calls from it yet (see `graphql-conventions`); it's not "unadopted," just unconsumed.
- Framework-specific patterns (this category's other notes) fill the gap between "what must be true" (OpenSpec) and "how it looks in Angular/Svelte" — an AI regenerating a client reads both, in that order.

See `doc/arc42/adr/0002-openspec-driven-client-generation.adoc` for why clients live in separate repositories at all.
