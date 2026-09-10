---
category: Architecture
type: moc
related:
  - ../index.md
status: current
updated: 2026-09-09
---

# Architecture — map of contents

Architecture-level patterns that explain *why* a cross-cutting structural choice looks the way it does. This category is deliberately thin: architecture **decisions** (ADRs) and **risks** are not Obsidian artifacts — they live under `doc/arc42/` (see the [ADR index](../../doc/arc42/09-architecture-decisions.adoc) and [risk register](../../doc/arc42/11-risks-and-technical-debt.adoc)) and are only ever linked to from here, never duplicated.

- [patterns/cqrs-style-rest-graphql-split.md](patterns/cqrs-style-rest-graphql-split.md) — why the backend exposes both REST and GraphQL for the same entities, and how the two stay behaviorally consistent.

↑ [Vault index](../index.md)
