# Ping Management

Manage lightweight reachability/heartbeat records — a minimal entity intentionally restricted to only the operations a heartbeat needs. References `security` instead of restating its rules; deviates from `rest-conventions` as noted below. No GraphQL API exists for this capability.

## REST Requirements

### Requirement: Ping model

A `Ping` has only `at` (timestamp), set to the current time by every update — no other properties. Like `Enum`, but unlike `Owner`/`Pet`/`Vet`/`Visit`, there is no `If-Match`/`ETag` concurrency control on `Ping` at all.

### Requirement: `PUT /api/ping/{id}` creates or refreshes a ping

Sets the `Ping` at the given id to the current time, creating it if it does not already exist. Takes no request body.

Reports `200 OK` if it already existed and was refreshed, `201 Created` if it was created, `400 Bad Request` if the data was not processed.

### Requirement: `GET /api/ping/{id}` gets one ping

Returns the `Ping` with the given id.

Reports `200 OK` if found, `404 Not Found` otherwise.

### Requirement: `POST /api/ping` is intentionally disabled

Creating a ping without a specific id is unsupported. Every request reports `405 Method Not Allowed`, regardless of its content. A specific ping is created via `PUT /api/ping/{id}` instead.

### Requirement: `GET /api/ping` is intentionally disabled

Listing every ping is unsupported. Every request reports `405 Method Not Allowed`, regardless of its content. A specific ping is retrieved via `GET /api/ping/{id}` instead.

### Requirement: `DELETE /api/ping/{id}` is intentionally disabled

Deleting a specific ping is unsupported. Every request reports `405 Method Not Allowed`, regardless of its content. Old pings are instead removed by a scheduled job depending on their age, not by an explicit delete.

### Requirement: Authorization

No endpoint under `/api/ping` currently requires authentication — see `security`.
