# App Info

Report the running application's identity and health over plain HTTP. References `security` instead of restating its rules. No persisted entity, no GraphQL. Unlike every other capability, none of these paths are under `/api`.

## REST Requirements

### Requirement: `GET /version` reports the running application's version

Loads the version from the application's packaged version file and returns it. Available as JSON (the default), or as plain AsciiDoc/HTML by adding a `.adoc`/`.html` suffix to the same path.

Reports `200 OK` if the version file is present and readable, `404 Not Found` if it is missing or unreadable, `400 Bad Request` if its content is syntactically invalid, `406 Not Acceptable` if the requested format cannot be produced.

### Requirement: `GET /home` is a trivial liveness probe

Reports `200 OK` with no body — a minimal signal that the process is accepting HTTP requests at all, independent of the deeper health checks below.

### Requirement: `GET /healthz` is a trivial liveness probe

Reports `200 OK` with no body — a minimal signal that the process is accepting HTTP requests at all, independent of the deeper health checks below.

### Requirement: `GET /` always reports not found

There is no resource at the root path; every request to it reports `404 Not Found`.

### Requirement: `GET /actuator/health` reports overall health

Reports the application's overall health status. Must be explicitly enabled in configuration — unlike the rest of this capability, it is not exposed by default.

### Requirement: `GET /actuator/health/liveness` reports whether the process is running

Reports the liveness state: whether the process itself is running, independent of whether it can yet serve requests. Must be explicitly enabled in configuration.

### Requirement: `GET /actuator/health/readiness` reports whether the process is ready to serve requests

Reports the readiness state: whether the process is ready to serve requests, distinct from merely being alive. Must be explicitly enabled in configuration.

### Requirement: Authorization

No endpoint under this capability currently requires authentication — see `security`.
