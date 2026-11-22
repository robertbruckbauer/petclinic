---
category: testing
related:
  - ../context/testing.md
status: current
updated: 2026-11-22
---

# Test pyramid in this repository

- **Unit/component tests** — JUnit 5 for backend (entity, repository, REST, GraphQL test classes per `AGENTS.md`'s artifact-location table). The large base of the pyramid; run on every change via `gradle check`.
- **API tests** — Playwright Java in `app/backend` drives the running HTTP API (REST) without a browser UI stack.

Each layer tests a different thing; an API-test failure for behavior a JUnit test could have caught cheaper is a signal the lower layer is missing coverage.
