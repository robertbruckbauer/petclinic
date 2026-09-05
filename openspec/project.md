# OpenSpec project context — petclinic

## What this system is

Pet Clinic is a veterinary clinic management system, implemented as a Self-Contained System: one backend server (REST + GraphQL APIs over a relational database) and one or more independent browser-based clients. See `doc/arc42` for the full architecture picture; this file gives an AI agent just enough to read/write specs correctly.

## Tech stack (for context only — specs below are technology-independent where possible)

- Backend: Java, Spring Boot, Spring Data JPA/Hibernate, QueryDSL, Spring Data REST, Spring GraphQL, Liquibase, Gradle.
- Database: HyperSQL (dev/test), PostgreSQL (prod).
- Clients today: Angular (`app/client-angular`) and Svelte (`app/client-svelte`), both REST-only. Standalone future clients: `petclinic-angular-ui`, `petclinic-svelte-ui`.

## Truth model and precedence

This repository has five artifacts that can describe the same fact. When two disagree, resolve in this order:

1. Code and tests (ground truth)
2. `doc/arc42` (bird's-eye view)
3. `doc/concept` (implementation guides)
4. `openspec` (this directory — the contract)
5. `obsidian` (explanatory knowledge graph — every note is a hypothesis about the code)

Full rule set: `doc/arc42/01-introduction-and-goals.adoc`.

`openspec` defines what *must* be true. It is normal for a capability spec to describe a target that isn't implemented yet, **as long as the gap is tracked** (an ADR under `doc/arc42/adr/` + a row in `doc/arc42/11-risks-and-technical-debt.adoc`'s risk table) — that's a documented gap, not a contradiction. See the `security` capability for the current example.

## Directory structure and workflow

```
openspec/
├── project.md      # this file
├── AGENTS.md        # agent instructions for working in this directory
└── specs/           # current, merged truth — one capability per folder
    └── <capability>/spec.md
```

`openspec/changes/` is **not a persisted repository artifact** — it's gitignored working state for drafting a change (`proposal.md`, `tasks.md`, optional `design.md`, a delta `specs/<capability>/spec.md`) before applying it straight into `specs/`. There is no `changes/archive/`; git history of `specs/` is the record of what changed and why. See `AGENTS.md` in this directory for the full propose → apply → merge workflow.

## Requirement format

Every requirement in a `specs/<capability>/spec.md` uses:

```
### Requirement: <short statement of what must be true>

<one or two sentences of context, if needed>

#### Scenario: <concrete situation>
- **GIVEN** <precondition>
- **WHEN** <action>
- **THEN** <observable outcome>
```

## Planned future changes (forward pointers)

- **Roles, permissions, and JWT resource server.** The `security` capability currently documents an unauthenticated baseline only. Introducing authorization is a separate future plan; its open questions are recorded in `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc` (status: `proposed / deferred`). Do not add roles/scopes/claims to `security/spec.md` until that ADR is accepted.
