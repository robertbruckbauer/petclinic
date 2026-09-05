---
category: Context
related: []
status: current
updated: 2026-09-05
---

# Entry point: testing

Minimum reading order for an agent asked to add or fix tests:

1. `obsidian/Testing/test-pyramid.md` — which layer the test belongs in before writing it.
2. The matching layer note: `vitest-conventions.md` (frontend unit/component), `playwright-e2e-conventions.md` (cross-stack E2E), or the backend test-class conventions already encoded in the relevant `.agents/skills/*/SKILL.md`.
3. `test-data-with-chance.md` / `../Database/test-data-initialization.md` only if the test needs fixture data beyond a single hand-written example.

`storybook-visual-testing.md` is relevant only when working in a standalone client repository, never in this repository.
