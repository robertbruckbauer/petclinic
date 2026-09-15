---
category: server
related:
  - entity-lifecycle-logging.md
status: current
updated: 2026-09-14
---

# Log level is static, not runtime-adjustable

Log verbosity for the whole backend is set only via `logging.level.*` keys in `app/server/src/main/resources/application.properties` (currently `org.springframework.web`, `org.springframework.web.cors`, `org.hibernate.SQL`, `org.hibernate.type.descriptor.sql`). Neither `lib/backend-data` nor `app/server` ships a `logback.xml`/`logback-spring.xml`/`log4j2.xml`, and Spring Boot Actuator's `/loggers` endpoint — the usual way to raise a logger's level at runtime without restarting — is not exposed (no `management.endpoints.web.exposure.include` lists `loggers`, and no `management.endpoint.loggers.enabled` is set). Debugging a production issue that needs more verbose logging therefore means editing the property and redeploying/restarting; there is no in-place toggle today.

Logging is an internal concept, not part of any OpenSpec capability. No dedicated `doc/concept` guide exists yet for log-level configuration; this note is currently the only written record.
