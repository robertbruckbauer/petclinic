---
category: Backend
related:
  - spring-data-rest-conventions.md
status: current
updated: 2026-09-08
---

# Entity/Item projection pattern

Every entity (`Owner`, `Pet`, `Vet`, ...) has a companion `{Entity}Item` class — a simplified projection carrying just enough fields to identify and display the entity in a selection list (e.g. `OwnerItem` for picking an owner while creating a pet). This is why `GET /api/{entity}/search/findAllItem` (per `openspec/specs/rest-conventions/spec.md`'s item-selection requirement) returns a different, lighter shape than `GET /api/{entity}` — it's a deliberate second read model for a specific UI need (item pickers), not an inconsistency between endpoints.

The same `{Entity}Item` classes have a second use beyond that dedicated endpoint: any entity's own JSON also embeds one wherever it has a relation, computed on the fly (e.g. `Pet`'s response carries `ownerItem`, not the `owner` relation itself, which is write-only). See `openspec/specs/rest-conventions/spec.md`'s relation-item requirement — same projection class, different call site.

Implementation baseline: `doc/concept/spring/_json-jpa-entity.adoc`.
