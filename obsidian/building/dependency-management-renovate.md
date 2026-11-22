---
category: building
related:
  - github-actions-workflows.md
status: current
updated: 2026-11-22
---

# Dependency management with Renovate

Dependency updates (Gradle plugins, Docker base images) are proposed automatically by Renovate, configured via `renovate.json` and `.github/renovate-config.js`, and run by `.github/workflows/renovate.yml`. Renovate covers the Gradle/JVM side plus Docker image digests used in `app/deploy`. Manual dependency bumps outside Renovate's PRs are the exception, not the norm — prefer letting Renovate propose the update and reviewing its PR.
