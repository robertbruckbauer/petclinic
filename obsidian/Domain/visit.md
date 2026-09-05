---
category: Domain
related:
  - pet.md
  - vet.md
  - glossary.md
status: current
updated: 2026-09-05
---

# Visit

A **Visit** is a single examination of a [Pet](pet.md) by a [Vet](vet.md) on a given date and time. A Visit records clinical notes, whether it's billable, and its duration — it is the clinic's record of what happened, not a scheduling/appointment concept (there is no separate "appointment" entity; a Visit is created once the examination itself is recorded).

For the exact contract, see the `visit-management` capability in `openspec/specs/visit-management/spec.md` and `doc/service/visit-restapi.adoc` / `doc/service/visit-graphql.adoc` for the generated API reference.
