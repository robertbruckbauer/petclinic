---
category: Domain
related:
  - visit.md
  - glossary.md
status: current
updated: 2026-09-05
---

# Vet

A **Vet** is a veterinarian employed by the clinic who conducts [Visits](visit.md). A Vet declares the skills and species they can treat, which is business information about clinic staffing/capability — not a permissions or access-control concept (that's a separate, currently unresolved question — see `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`).

For the exact contract, see the `vet-management` capability in `openspec/specs/vet-management/spec.md` and `doc/service/vet-restapi.adoc` / `doc/service/vet-graphql.adoc` for the generated API reference.
