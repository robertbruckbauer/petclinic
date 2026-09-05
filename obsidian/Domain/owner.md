---
category: Domain
related:
  - pet.md
  - glossary.md
  - ../Architecture/patterns/cqrs-style-rest-graphql-split.md
status: current
updated: 2026-09-05
---

# Owner

An **Owner** is a client of the clinic — the person who brings pets in for treatment. Every [Pet](pet.md) belongs to exactly one Owner; an Owner may have any number of Pets.

Owners are managed directly by clinic staff through the browser clients — there is currently no owner self-service login. Whether that changes is an open question tracked in `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc`.

For the exact fields, endpoints, and validation rules, see the `owner-management` capability in `openspec/specs/owner-management/spec.md` (normative) and `doc/service/owner-restapi.adoc` / `doc/service/owner-graphql.adoc` (generated API reference) — not restated here.
