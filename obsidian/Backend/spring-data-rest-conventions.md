---
category: Backend
related:
  - querydsl-repository-pattern.md
  - controller-advice-error-mapping.md
  - entity-item-projection-pattern.md
status: current
updated: 2026-09-05
---

# Spring Data REST conventions used in this backend

Every entity's REST controller (`{Entity}RestController`) is a custom Spring MVC `@RestController`, not an auto-exposed Spring Data REST repository — this gives explicit control over ETag handling, PATCH-as-merge semantics, and error mapping, per `openspec/specs/rest-conventions/spec.md`. `EsyBackendRestControllerAdvice` (see `controller-advice-error-mapping.md`) is shared across all entity controllers so error shapes stay consistent without each controller reimplementing them.

Implementation baseline for adding a new entity's REST layer: `doc/concept/spring/_json-jpa-rest-controller.adoc` (used by the `domain-entity-creator` skill). This note explains *why* the pattern looks the way it does; the `.adoc` file is the step-by-step implementation guide — not duplicated here.
