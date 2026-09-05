---
category: Database
related:
  - ../Testing/test-data-with-chance.md
status: current
updated: 2026-09-05
---

# Test data initialization

`ServerTestset` (`app/server/src/main/java/esy/ServerTestset.java`, per `AGENTS.md`'s artifact-location table) seeds a running server with representative Owner/Pet/Vet/Visit data for manual exploration and Playwright end-to-end tests — separate from the unit-level test fixtures each backend test class builds for itself. This is the one place "what does a freshly started demo system contain" is decided; individual entity tests do not rely on it being present.
