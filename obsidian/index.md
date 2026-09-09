---
type: index
related:
  - Architecture/moc.md
  - Backend/moc.md
  - Building/moc.md
  - Context/moc.md
  - Database/moc.md
  - Domain/moc.md
  - Frontend/moc.md
  - Security/moc.md
  - Testing/moc.md
status: current
updated: 2026-09-09
---

# Obsidian knowledge graph — vault index

Entry point into this vault. Each of the nine categories has its own map of contents (MOC) linking and briefly describing its notes — this page only links the nine MOCs, it does not duplicate their content. See `AGENTS.md`'s "Documentation" section for how this graph relates to `doc/arc42`, `openspec`, and the other documentation systems, and for the truth-model precedence order.

- [Domain/moc.md](Domain/moc.md) — the clinic's core entities (Owner, Pet, Vet, Visit) and their relationships.
- [Architecture/moc.md](Architecture/moc.md) — cross-cutting structural patterns; decisions and risks live in `doc/arc42`, not here.
- [Backend/moc.md](Backend/moc.md) — Spring REST/GraphQL backend patterns and conventions.
- [Frontend/moc.md](Frontend/moc.md) — framework-independent UI patterns shared by both clients.
- [Security/moc.md](Security/moc.md) — access-control-adjacent code as it exists today.
- [Database/moc.md](Database/moc.md) — schema, migration, and test-data conventions.
- [Testing/moc.md](Testing/moc.md) — which test layer a change belongs in, and each layer's conventions.
- [Building/moc.md](Building/moc.md) — build tooling and CI/CD conventions.
- [Context/moc.md](Context/moc.md) — curated minimum-reading-order entry points per task shape.

New notes are added to their category's MOC in the same change that adds the note — see the `obsidian-maintainer` skill.
