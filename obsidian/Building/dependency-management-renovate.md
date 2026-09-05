---
category: Building
related:
  - github-actions-workflows.md
status: current
updated: 2026-09-05
---

# Dependency management with Renovate

Dependency updates (Gradle plugins, npm packages, Docker base images) are proposed automatically by Renovate, configured via `renovate.json` and `.github/renovate-config.js`, and run by `.github/workflows/renovate.yml`. Recent history (e.g. "Update angular monorepo to v22.1.4", "Update postgres:18 Docker digest") shows Renovate covers both the Gradle/JVM side and the two clients' npm dependencies, plus Docker image digests used in `app/deploy`. Manual dependency bumps outside Renovate's PRs are the exception, not the norm — prefer letting Renovate propose the update and reviewing its PR.
