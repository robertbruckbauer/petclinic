# Backend Platform

Technology stack for the backend modules — `lib/backend-api`, `lib/backend-data`, `app/server`, `app/migrate` — as currently implemented. Per `openspec/AGENTS.md`, any capability suffixed `-platform` is technology-specific by design: the requirements here are about the technology itself (Gradle module boundaries, how each module is packaged), not an abstraction over it. Module responsibilities are defined once in `doc/arc42/05-building-block-view.adoc`; this file does not restate them, only the build/runtime behavior that follows from them. The database engine and migration mechanism are `database-platform`'s job, not this file's.

## Requirements

### Requirement: Module dependency direction is one-way, from platform up to runnable app

`lib/backend-api` depends on no other module in this repository. `lib/backend-data` depends on `lib/backend-api` (a compile-time `api` dependency, so `backend-api`'s types are visible to anything depending on `backend-data`). `app/server` and `app/migrate` each depend on `lib/backend-data` only — neither depends on the other, and neither is depended on by any `lib` module.

#### Scenario: backend-api builds without the web/data/security stack
- **GIVEN** `lib/backend-api`'s build file
- **WHEN** its dependencies are resolved
- **THEN** it pulls in no Spring Boot starter, no Liquibase, and no database driver — only Spring Framework core, Spring HATEOAS, QueryDSL, GraphQL-Java, Jakarta Persistence/Transaction, and Hibernate Validator

#### Scenario: app/migrate reuses backend-data's dependencies instead of declaring its own
- **GIVEN** `app/migrate`'s build file
- **WHEN** its dependencies are resolved
- **THEN** its JDBC drivers (HyperSQL, PostgreSQL) and Liquibase classes come from depending on `lib:backend-data`, not from `app/migrate` declaring the REST/GraphQL/security stack itself

### Requirement: Packaging differs per module by its role

`app/server` is the only module packaged as a Spring Boot executable (`bootJar`) and built into a container image via both Spring Boot Cloud Native Buildpacks and Jib. `app/migrate` builds a container image via Jib only — its `bootJar` and Buildpacks image are disabled, so it ships as a plain Java `main` class (`esy.migrate.MigrateRunner`) despite being built from a Spring Boot Gradle module. `lib/backend-api` and `lib/backend-data` are packaged only as plain library jars (`java-library`) — neither produces a `bootJar`, a Buildpacks image, or a Jib image; neither is ever run standalone.

#### Scenario: migrate's image runs a plain main class, not a Spring Boot application
- **GIVEN** the `app/migrate` container image
- **WHEN** it is run
- **THEN** it executes `esy.migrate.MigrateRunner.main`, with no embedded web server and no Spring application context started

### Requirement: Every module's build applies the same formatting and versioning conventions

All four modules apply `com.diffplug.spotless` for Java formatting (leading tabs to spaces, unused-import removal) and read the repository's single root `VERSION` file into their own build output — there is no per-module version.

#### Scenario: A version bump propagates to every module without editing each one
- **GIVEN** the root `VERSION` file
- **WHEN** any of the four modules is built
- **THEN** its packaged artifact (jar manifest, container image tag, or embedded resource) reflects the root `VERSION` value, not a value declared in that module
