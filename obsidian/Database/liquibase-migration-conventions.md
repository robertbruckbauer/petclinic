---
category: Database
related:
  - relational-model-overview.md
status: current
updated: 2026-09-05
---

# Liquibase migration conventions

Schema changes are managed entirely through Liquibase changesets under `lib/backend-data/src/main/resources/liquibase/v1/`, one file per table, referenced from `changelog.xml` in order. There is no manual schema management outside Liquibase — the schema in any environment (HSQLDB dev/test, PostgreSQL prod) is whatever the changelog produces.

Adding a new entity adds a new changeset file with a `preCondition` guarding `tableExists`; adding a property to an existing entity adds a new changeset without preconditions. Implementation baseline: `doc/concept/spring/_json-jpa-entity.adoc` (used by `domain-entity-creator` and `domain-entity-property-adder`).
