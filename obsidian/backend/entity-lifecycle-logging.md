---
category: backend
related:
  - spring-data-rest-conventions.md
  - controller-advice-error-mapping.md
status: current
updated: 2026-11-24
---

# Entity lifecycle logging

The REST event handlers for Owner, Pet, Vet, and Visit extend [JsonJpaRestControllerBase](../../lib/backend-data/src/main/java/esy/rest/JsonJpaRestControllerBase.java), which logs their repository lifecycle events.
Enum and Ping use separate controllers and do not have handlers extending that base class.
Do not assume the shared lifecycle logging applies to every entity.

See the [REST controller implementation guide](../../doc/concept/spring/_json-jpa-rest-controller.adoc).
