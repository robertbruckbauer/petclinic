# Security

**Baseline, as-implemented today.** This capability intentionally does not yet specify roles, scopes, claims, or JWT validation. Introducing those is a separate future plan — see `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc` (status: `proposed / deferred`) and the risk table in `doc/arc42/11-risks-and-technical-debt.adoc`. When that plan is accepted, it adds a `## ADDED Requirements` delta to this file; it does not replace the requirements below.

## Requirements

### Requirement: No endpoint currently requires authentication

Every `/api/**` REST endpoint and the `/graphql` endpoint accept unauthenticated requests. There is no login flow, no bearer token validation, and no `oauth2ResourceServer` configuration.

#### Scenario: Unauthenticated request succeeds
- **GIVEN** a request to any `/api/**` endpoint or `/graphql` with no `Authorization` header
- **WHEN** the request is otherwise valid
- **THEN** it is processed normally — no `401 Unauthorized` is returned

This is a tracked, accepted risk (see the risk table in `doc/arc42/11-risks-and-technical-debt.adoc`), not an oversight in this spec.

### Requirement: Static resources, health, and version endpoints are always exempt

`/static/**`, the health endpoint(s), and the version endpoint are never subject to authentication, even after the future security plan lands — they must stay reachable for infrastructure/monitoring regardless of the authorization model.

### Requirement: Sessions are stateless

No server-side session state is created; every request is handled independently. CSRF protection is disabled (there is no session-based form submission to protect).

### Requirement: CORS is enabled with a fixed, explicit policy

- Allowed origins: `http://localhost:[*]`, `http://localhost`, `https://localhost`, `https://*.cardsplus.info`, `https://*.github.dev`.
- Credentials are allowed.
- Allowed headers: `Accept`, `Authorization`, `Content-Type`, `Content-Length`, `If-Match`, `If-None-Match`.
- Allowed methods: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`.
- Max age: 3600 seconds.
- Private Network Access (`Access-Control-Request-Private-Network`) is allowed, to support local development across network boundaries (e.g. devcontainers).

#### Scenario: A disallowed origin is rejected by the browser, not the server
- **GIVEN** a browser page served from an origin not in the allowed list
- **WHEN** it makes a cross-origin request to the backend
- **THEN** the browser blocks the response based on the missing CORS headers (the server itself still processes the request; CORS is a browser-enforced boundary, not a server-side authorization check)

### Requirement (planned, not yet active): JWT resource server

Once the future security plan (ADR 0004) is accepted, this requirement replaces "No endpoint currently requires authentication" above:

- The backend validates a bearer JWT on every non-exempt endpoint.
- Authorization rules (which roles/scopes/claims permit which operations, per entity) are defined by that plan, not guessed here.

Do not implement `oauth2ResourceServer(...)` or add roles/scopes to client code against this placeholder — it is not yet a requirement, only a documented forward pointer.
