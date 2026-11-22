---
category: building
related:
  - github-actions-workflows.md
status: current
updated: 2026-11-22
---

# Gradle module conventions

`settings.gradle` includes: `lib:backend-api`, `lib:backend-data`, `app:backend`, `app:migrate`, `app:deploy`. Root `build.gradle` applies shared conventions to every subproject (Java 25 source/target compatibility, JUnit Platform excluding the vintage engine, UTF-8 everywhere) rather than each module repeating them. Version management (`gradle versionTag`, `versionCheck`, `versionHistory`, `versionRelease`) and changelog generation (`buildChangelog`) are custom Gradle tasks defined in `buildSrc`, driven off the root `VERSION` file — see `doc/manual/setup-version.adoc`.

Adding plain directories (`plans/`, `openspec/`, `obsidian/`, `doc/arc42/`) never requires a `settings.gradle` change — only Gradle modules need registering.
