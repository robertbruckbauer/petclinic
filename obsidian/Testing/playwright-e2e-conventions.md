---
category: Testing
related:
  - test-pyramid.md
status: current
updated: 2026-09-05
---

# Playwright end-to-end conventions

`playwright.test.sh` brings up `client-angular`, `client-svelte`, and `server` via Docker Compose, waits for each service's `/healthz` to respond, then runs `npx playwright test` inside each client directory in turn, publishing HTML reports under `pages/html/{client}/playwright`. Both clients keep their Playwright specs under `src/test/playwright/`, one file per entity (`{entity}.test.ts`, per `AGENTS.md`'s artifact-location tables).

This is the one layer that exercises the real backend, not a mock — reserve it for cross-stack behavior (a full create-then-view-then-edit flow), not for validation-message wording that a Vitest component test already covers cheaper.
