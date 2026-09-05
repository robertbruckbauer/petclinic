---
category: Building
related:
  - node-tooling-conventions.md
status: current
updated: 2026-09-05
---

# Gradle module conventions

`settings.gradle` includes: `lib:backend-api`, `lib:backend-data`, `app:server`, `app:migrate`, `app:client-svelte`, `app:client-angular`, `app:deploy`, `app:tester`. Root `build.gradle` applies shared conventions to every subproject (Java 25 source/target compatibility, JUnit Platform excluding the vintage engine, UTF-8 everywhere) rather than each module repeating them. Version management (`gradle versionTag`, `versionCheck`, `versionHistory`, `versionRelease`) and changelog generation (`buildChangelog`) are custom Gradle tasks defined in `buildSrc`, driven off the root `VERSION` file — see `doc/manual/setup-version.adoc`.

Adding plain directories (`plans/`, `openspec/`, `obsidian/`, `doc/arc42/`) never requires a `settings.gradle` change — only Java/Node modules need registering.
