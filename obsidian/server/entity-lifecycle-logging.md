---
category: server
related:
  - spring-data-rest-conventions.md
  - controller-advice-error-mapping.md
status: current
updated: 2026-09-14
---

# Entity lifecycle logging

Every entity's REST CRUD lifecycle (create, update, link, delete, unlink) is logged the same way because `JsonJpaRestControllerBase` (`lib/backend-data/src/main/java/esy/rest/JsonJpaRestControllerBase.java`) implements all ten Spring Data REST `@Handle*` event hooks itself, and every entity's own event handler (Owner, Pet, Vet, Visit, Enum, Ping) extends it instead of adding logging of its own. A "before" hook logs `RECEIVED [{}]; <verb>ing ...` at INFO; the matching "after" hook logs `ACCEPTED`/`CREATED`/`UPDATED`/`DELETED [{}]` at INFO — always via SLF4J `{}` placeholders, never string concatenation. A new entity type gets this logging for free the moment its event handler extends the base class; there is nothing to add or remember to wire up per entity.

Logging is an internal concept, not part of any OpenSpec capability. Normative: `doc/concept/spring/_json-jpa-rest-controller.adoc`.
