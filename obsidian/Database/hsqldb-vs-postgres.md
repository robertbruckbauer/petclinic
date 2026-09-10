---
category: Database
related:
  - liquibase-migration-conventions.md
status: current
updated: 2026-09-05
---

# HyperSQL vs. PostgreSQL

The same Liquibase changelog runs against two database engines: HyperSQL (`hsqldb`, in-memory, `app/deploy/compose.yml`'s default) for local development/tests, and PostgreSQL (`app/deploy/compose-pg.yml`) as the production-shaped target. Liquibase's changesets are written to be portable across both — this is why the schema is expressed through Liquibase's abstract column types rather than engine-specific SQL. Any behavior that differs between the two engines is a real risk worth a row in `doc/arc42/11-risks-and-technical-debt.adoc`'s risk table the moment it's discovered; none is known/tracked as of this note.
