---
category: Testing
related:
  - vitest-conventions.md
  - playwright-e2e-conventions.md
status: current
updated: 2026-09-05
---

# Test pyramid in this repository

- **Unit/component tests** — JUnit 5 for backend (entity, repository, REST, GraphQL test classes per `AGENTS.md`'s artifact-location table), Vitest for both frontend clients (`vitest run --coverage` in each client's `package.json`). The large base of the pyramid; run on every change.
- **End-to-end tests** — Playwright, driving the real backend + both clients together via Docker Compose (`playwright.test.sh`). Verifies cross-stack behavior a unit test can't (real HTTP, real browser rendering).
- **Visual/style tests** — planned for the standalone client repos only (Storybook), not this repository — see `storybook-visual-testing.md`.

Each layer tests a different thing; a Playwright failure for behavior a Vitest/JUnit test could have caught cheaper is a signal the lower layer is missing coverage, not that Playwright is doing its job.
