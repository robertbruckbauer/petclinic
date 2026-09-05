---
category: Testing
related:
  - test-pyramid.md
  - test-data-with-chance.md
status: current
updated: 2026-09-05
---

# Vitest conventions

Both clients run `vitest run --coverage` (via `@vitest/coverage-v8`) as their `npm test` script. Test files sit next to what they test (`{entity}.service.test.ts` beside `{entity}.service.ts`, per `AGENTS.md`'s artifact-location tables), not in a separate mirrored tree. This keeps a component/service and its test moving together as the entity evolves (e.g. `domain-entity-property-adder`-style changes touch both in the same step).
