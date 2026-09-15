---
type: index
related:
  - architecture/moc.md
  - server/moc.md
  - building/moc.md
  - context/moc.md
  - database/moc.md
  - domain/moc.md
  - client/moc.md
  - security/moc.md
  - testing/moc.md
status: current
updated: 2026-09-09
---

# Obsidian knowledge graph — vault index

Entry point into this vault. Each of the nine categories has its own map of contents (MOC) linking and briefly describing its notes — this page only links the nine MOCs, it does not duplicate their content. See `AGENTS.md`'s "Documentation" section for how this graph relates to `doc/arc42`, `openspec`, and the other documentation systems, and for the truth-model precedence order.

- [domain/moc.md](domain/moc.md) — the clinic's core entities (Owner, Pet, Vet, Visit) and their relationships.
- [architecture/moc.md](architecture/moc.md) — cross-cutting structural patterns; decisions and risks live in `doc/arc42`, not here.
- [server/moc.md](server/moc.md) — Spring REST/GraphQL backend patterns and conventions.
- [client/moc.md](client/moc.md) — framework-independent UI patterns shared by both clients.
- [security/moc.md](security/moc.md) — access-control-adjacent code as it exists today.
- [database/moc.md](database/moc.md) — schema, migration, and test-data conventions.
- [testing/moc.md](testing/moc.md) — which test layer a change belongs in, and each layer's conventions.
- [building/moc.md](building/moc.md) — build tooling and CI/CD conventions.
- [context/moc.md](context/moc.md) — curated minimum-reading-order entry points per task shape.

New notes are added to their category's MOC in the same change that adds the note — see the `obsidian-maintainer` skill.
