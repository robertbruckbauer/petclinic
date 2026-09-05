---
category: Context
related: []
status: current
updated: 2026-09-05
---

# Entry point: generating or rebuilding a client

Minimum reading order for an agent asked to generate or rebuild a browser client (Angular, Svelte, or a future stack):

1. `openspec/project.md` — truth model, then `openspec/specs/client-shell/spec.md` and the relevant per-entity capability's `## UI Requirements` / `## REST Requirements` (and `## GraphQL Requirements` only if the target client adopts GraphQL).
2. `doc/arc42/adr/0002-openspec-driven-client-generation.adoc` — why clients live in separate repos and how the sync mechanism works.
3. `obsidian/Frontend/client-generation-from-openspec.md`, `lister-editor-viewer-pattern.md`, `rxjs-service-pattern.md`, `etag-optimistic-concurrency-ui.md` — the framework-independent patterns every generated screen follows.

Do not also read `doc/service/*.adoc` or `doc/concept/*` up front — those are implementation-detail references to pull in only if a specific field/behavior needs Spring-side confirmation.
