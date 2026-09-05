---
category: Database
related:
  - liquibase-migration-conventions.md
  - ../Domain/owner.md
status: current
updated: 2026-09-05
---

# Relational model overview

Every entity table has `id` (`UUID`, primary key) and `version` (`BIGINT`, optimistic-locking counter backing the REST layer's ETag — see `openspec/specs/rest-conventions/spec.md`). Relationships mirror the domain model: `pet.owner_id` (many-to-one to owner), `visit.pet_id` / `visit.vet_id` (many-to-one to pet/vet). `vet_skill` and `vet_species` are child tables for Vet's multi-value collections, uniqueness enforced at the application layer rather than a database constraint (see `doc/service/vet-restapi.adoc`'s note on this).

For exact columns, see the Liquibase changesets under `lib/backend-data/src/main/resources/liquibase/v1/` (ground truth) — not restated here.
