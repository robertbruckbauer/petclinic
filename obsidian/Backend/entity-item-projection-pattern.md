---
category: Backend
related:
  - spring-data-rest-conventions.md
status: current
updated: 2026-09-05
---

# Entity/Item projection pattern

Every entity (`Owner`, `Pet`, `Vet`, ...) has a companion `{Entity}Item` class — a simplified projection carrying just enough fields to identify and display the entity in a selection list (e.g. `OwnerItem` for picking an owner while creating a pet). This is why `GET /api/{entity}/search/findAllItem` (per `openspec/specs/rest-conventions/spec.md`'s item-selection requirement) returns a different, lighter shape than `GET /api/{entity}` — it's a deliberate second read model for a specific UI need (item pickers), not an inconsistency between endpoints.

Implementation baseline: `doc/concept/spring/_json-jpa-entity.adoc`.
