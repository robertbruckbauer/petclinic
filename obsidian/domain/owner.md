---
category: domain
related:
  - pet.md
  - glossary.md
  - ../architecture/patterns/cqrs-style-rest-graphql-split.md
status: current
updated: 2026-09-05
---

# Owner

An **Owner** is a client of the clinic — the person who brings pets in for treatment. Every [Pet](pet.md) belongs to exactly one Owner; an Owner may have any number of Pets.

Owners are managed directly by clinic staff through the HTTP API.

For the exact fields, endpoints, and validation rules, see the `backend-api-owner` capability in `openspec/specs/backend/backend-api-owner/spec.md` (normative) and `doc/service/owner-restapi.adoc` / `doc/service/owner-graphql.adoc` (generated API reference) — not restated here.
