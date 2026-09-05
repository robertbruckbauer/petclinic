---
category: Architecture
related:
  - ../../../doc/arc42/adr/0003-rest-and-graphql-duality.adoc
  - ../../Backend/graphql-batch-loading.md
status: current
updated: 2026-09-05
---

# Pattern: CQRS-flavored REST/GraphQL split

This system is not a textbook CQRS implementation (there's one database, one write model, no event sourcing) — but the REST/GraphQL split has a CQRS *flavor* worth naming explicitly, because it explains why two API surfaces exist for the same entities without duplicating their contract:

- **Commands** (create/update/delete) always go through REST (`POST`/`PUT`/`PATCH`/`DELETE`), per `openspec/specs/rest-conventions/spec.md`.
- **Queries** can go through either REST `GET` or GraphQL — GraphQL exists specifically because it lets a query shape its own response (e.g. an owner with only the pet fields it needs), which a fixed REST representation can't do without extra endpoints.

The two query paths are kept behaviorally consistent by having `openspec/specs/graphql-conventions/spec.md` reference `rest-conventions` for shared behavior (validation, error classification) rather than defining it a second time — see `doc/arc42/adr/0003-rest-and-graphql-duality.adoc` for why this split exists at all.
