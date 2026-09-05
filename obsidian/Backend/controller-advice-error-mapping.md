---
category: Backend
related:
  - spring-data-rest-conventions.md
status: current
updated: 2026-09-05
---

# Controller advice error mapping

`EsyBackendRestControllerAdvice` (`lib/backend-data/src/main/java/esy/app/EsyBackendRestControllerAdvice.java`) is the single `@ControllerAdvice` shared by every entity's REST controller. It maps specific exceptions to specific statuses — for example `ETagDoesntMatchException` → `412 Precondition Failed`, with a message telling the caller to reload — rather than letting each controller catch and translate its own exceptions. This is the concrete mechanism behind `openspec/specs/rest-conventions/spec.md`'s "Errors use a consistent shape and error code mapping" requirement: adding a new entity does not mean re-deciding what status a stale write or a not-found lookup returns.

Implementation baseline: `doc/concept/spring/endpoint.adoc`.
