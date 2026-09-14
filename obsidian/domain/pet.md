---
category: domain
related:
  - owner.md
  - visit.md
  - glossary.md
status: current
updated: 2026-09-05
---

# Pet

A **Pet** is an animal brought to the clinic for treatment, belonging to exactly one [Owner](owner.md). A Pet accumulates any number of [Visits](visit.md) over its lifetime, each conducted by a [Vet](vet.md).

A Pet's species and sex are business attributes used to tailor treatment, not free-text notes — see the `backend-api-pet` capability in `openspec/specs/backend-api-pet/spec.md` for the exact contract, and `doc/service/pet-restapi.adoc` / `doc/service/pet-graphql.adoc` for the generated API reference. The UI built on it is `client-ui-pet`.
