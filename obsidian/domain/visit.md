---
category: domain
related:
  - pet.md
  - vet.md
  - glossary.md
status: current
updated: 2026-09-08
---

# Visit

A **Visit** is a single examination of a [Pet](pet.md) by a [Vet](vet.md) on a given date and time. A Visit records clinical notes, whether it's billable, and its duration — it is the clinic's record of what happened, not a scheduling/appointment concept (there is no separate "appointment" entity; a Visit is created once the examination itself is recorded).

Only `date` is actually required — a Visit's `pet` and `vet` are both optional relations, so a Visit can exist as a placeholder (e.g. for a vet on duty) before either is known. The shipped UI always sets `pet` at creation (via the pet screens' "Add a treatment" action) but commonly omits `vet` until the Diagnose form assigns one later — so a pet-less Visit is a real backend capability that just isn't exercised by any current screen.

For the exact contract, see the `backend-api-visit` capability in `openspec/specs/backend-api-visit/spec.md` and `doc/service/visit-restapi.adoc` / `doc/service/visit-graphql.adoc` for the generated API reference. The UI built on it is `client-ui-visit`.
