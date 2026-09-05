---
category: Backend
related:
  - ../../doc/arc42/adr/0003-rest-and-graphql-duality.adoc
  - ../Architecture/patterns/cqrs-style-rest-graphql-split.md
status: current
updated: 2026-09-05
---

# GraphQL batch loading for relations

Every GraphQL controller (e.g. `OwnerGraphqlController`, `PetGraphqlController`) declares its top-level queries with `@QueryMapping` and its relation fields with `@BatchMapping`, not `@SchemaMapping` resolved one-by-one. `@BatchMapping` lets Spring GraphQL collect all the parent ids from one query response and resolve the relation (e.g. every owner's `allPet`) in a single batched call, avoiding an N+1 query pattern when a client asks for a collection plus a nested relation.

This is the concrete mechanism behind `openspec/specs/graphql-conventions/spec.md`'s "Relations are resolved with batch loading" requirement. Implementation baseline: `doc/concept/spring/_graphql-controller.adoc`.
