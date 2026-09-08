---
category: Backend
related:
  - querydsl-repository-pattern.md
  - controller-advice-error-mapping.md
  - entity-item-projection-pattern.md
status: current
updated: 2026-09-08
---

# Spring Data REST conventions used in this backend

For `Owner`/`Pet`/`Vet`/`Visit`, the full REST CRUD surface (collection/item `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, ETag/`If-Match` from the `@Version` field) is **auto-exposed by Spring Data REST** from an `@RepositoryRestResource`-annotated `{Entity}Repository` — none of it is hand-written. The companion `{Entity}RestController` does *not* implement any HTTP verb itself: it's a `@RepositoryEventHandler` + `@BasePathAwareController` extending the shared `JsonJpaRestControllerBase`, which only hooks into the repository's create/save/delete lifecycle (`beforeCreate`/`afterCreate`/`beforeSave`/`afterSave`/`beforeDelete`/`afterDelete`) to call the entity's own `verify()` validation and publish a domain event — a validation/eventing hook, not the endpoint implementation. `EsyBackendRestControllerAdvice` (see `controller-advice-error-mapping.md`) is shared across every entity so error shapes stay consistent regardless of how each one's endpoint is implemented.

`Enum` and `Ping` are the exception: both repositories are `@RepositoryRestResource(exported = false)` (not auto-exposed at all) and are instead served by a fully hand-written `@RestController` (`EnumRestController`, `PingRestController`) that implements every operation itself — and neither has any ETag/`If-Match` concurrency control, unlike the four auto-exposed entities. See `openspec/specs/rest-conventions/spec.md`, `openspec/specs/enum-management/spec.md`, and `openspec/specs/ping-management/spec.md`.

Implementation baseline for adding a new entity's REST layer: `doc/concept/spring/_json-jpa-rest-controller.adoc` (used by the `domain-entity-creator` skill). This note explains *why* the pattern looks the way it does; the `.adoc` file is the step-by-step implementation guide — not duplicated here.
