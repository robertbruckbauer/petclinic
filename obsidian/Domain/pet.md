---
category: Domain
related:
  - owner.md
  - visit.md
  - glossary.md
status: current
updated: 2026-09-05
---

# Pet

A **Pet** is an animal brought to the clinic for treatment, belonging to exactly one [Owner](owner.md). A Pet accumulates any number of [Visits](visit.md) over its lifetime, each conducted by a [Vet](vet.md).

A Pet's species and sex are business attributes used to tailor treatment, not free-text notes — see the `pet-management` capability in `openspec/specs/pet-management/spec.md` for the exact contract, and `doc/service/pet-restapi.adoc` / `doc/service/pet-graphql.adoc` for the generated API reference.
