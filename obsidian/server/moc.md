---
category: server
type: moc
related:
  - ../index.md
status: current
updated: 2026-09-14
---

# Server — map of contents

Patterns and conventions behind the Spring-based REST/GraphQL backend (`lib/backend-data`, `app/server`).

- [spring-data-rest-conventions.md](spring-data-rest-conventions.md) — how Owner/Pet/Vet/Visit's REST CRUD is auto-exposed by Spring Data REST, and how Enum/Ping are the hand-written exception.
- [querydsl-repository-pattern.md](querydsl-repository-pattern.md) — the shared repository base interfaces behind type-safe QueryDSL filtering.
- [entity-item-projection-pattern.md](entity-item-projection-pattern.md) — the `{Entity}Item` projection used for item pickers and embedded relations.
- [controller-advice-error-mapping.md](controller-advice-error-mapping.md) — the single `@ControllerAdvice` mapping exceptions to consistent HTTP error statuses.
- [entity-lifecycle-logging.md](entity-lifecycle-logging.md) — the uniform RECEIVED/ACCEPTED/CREATED/UPDATED/DELETED INFO logging every entity gets for free via `JsonJpaRestControllerBase`.
- [log-level-configuration.md](log-level-configuration.md) — log verbosity is fixed via `application.properties`, with no config file and no runtime Actuator toggle — changing it means editing and restarting.
- [graphql-batch-loading.md](graphql-batch-loading.md) — why relation fields resolve via `@BatchMapping` instead of one-by-one, avoiding N+1 queries.

↑ [Vault index](../index.md)
