---
category: Database
type: moc
related:
  - ../index.md
status: current
updated: 2026-09-09
---

# Database — map of contents

Schema, migration, and test-data conventions for the Liquibase-managed relational store.

- [relational-model-overview.md](relational-model-overview.md) — the shared `id`/`version` columns and how relationships mirror the domain model.
- [liquibase-migration-conventions.md](liquibase-migration-conventions.md) — how schema changes are made, one changeset file per table.
- [hsqldb-vs-postgres.md](hsqldb-vs-postgres.md) — why the same changelog must stay portable across both database engines.
- [test-data-initialization.md](test-data-initialization.md) — where the demo/Playwright seed dataset is defined.

↑ [Vault index](../index.md)
