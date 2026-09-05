---
category: Building
related:
  - dependency-management-renovate.md
  - gradle-module-conventions.md
status: current
updated: 2026-09-05
---

# GitHub Actions workflows

`.github/workflows/build.yml` runs on push to `master`/`main`, on version tags, and on pull requests: version check → build both clients' Node projects → `gradle spotlessCheck` + `gradle build --parallel` → test/coverage reports → arc42/service pages (`gradle pages`) → container images (`gradle buildImage`) → Playwright end-to-end suite. On a tag push it additionally publishes GitHub Pages, pushes container images, and creates a GitHub Release from the generated changelog. `.github/workflows/renovate.yml` runs Renovate separately — see `dependency-management-renovate.md`.
