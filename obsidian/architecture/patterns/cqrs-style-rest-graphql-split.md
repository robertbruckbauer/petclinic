---
category: architecture
related:
  - ../../../doc/arc42/adr/0006-graphql-transactional-mutation-experiment.adoc
  - ../../server/graphql-batch-loading.md
status: current
updated: 2026-11-04
---

# Pattern: CQRS-flavored REST/GraphQL split

This system is not a textbook CQRS implementation (there's one database, one write model, no event sourcing) — but the REST/GraphQL split has a CQRS *flavor* worth naming explicitly, because it explains why two API surfaces exist for the same entities without duplicating their contract:

- **Commands** go through REST; the one GraphQL exception is decided in [ADR 0006](../../../doc/arc42/adr/0006-graphql-transactional-mutation-experiment.adoc).
- **Queries** can go through either REST `GET` or GraphQL — GraphQL exists specifically because it lets a query shape its own response (e.g. an owner with only the pet fields it needs), which a fixed REST representation can't do without extra endpoints.

The two query paths share the [API conventions](../../../openspec/specs/backend-api-conventions/spec.md), including their validation and error-classification requirements; [ADR 0006](../../../doc/arc42/adr/0006-graphql-transactional-mutation-experiment.adoc) explains why both APIs exist.
