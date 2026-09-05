---
category: Context
related: []
status: current
updated: 2026-09-05
---

# Entry point: backend development

Minimum reading order for an agent changing backend code (entities, repositories, REST/GraphQL controllers):

1. The relevant `.agents/skills/*/SKILL.md` for the change shape (`domain-entity-creator`, `domain-entity-property-adder`, relation adders, etc.) — these already encode the concrete implementation baselines (`doc/concept/spring/*.adoc`) to follow.
2. `obsidian/Backend/` notes relevant to the change: `spring-data-rest-conventions.md`, `querydsl-repository-pattern.md`, `entity-item-projection-pattern.md`, `controller-advice-error-mapping.md`, `graphql-batch-loading.md` — pick only the ones touching the change, not all five.
3. `openspec/specs/rest-conventions/spec.md` and/or `graphql-conventions/spec.md` — confirm the change doesn't contradict the normative contract; if it does, that's an OpenSpec change first (`openspec-maintainer`), not a silent behavior drift.

Do not start from `doc/arc42` for this task — it's bird's-eye and won't have the detail needed to write code.
