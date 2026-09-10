# Agent instructions for `doc/concept/`

## Role of this directory

`doc/concept` contains detailed implementation guides for the technologies used in this repository. They describe the proven implementation patterns; skills operationalize a guide into a reproducible workflow for AI agents. Where `doc/arc42` deliberately stays at the level a stakeholder needs, `doc/concept` is deliberately far more technical and detailed — it exists to be read and applied down to code, class, and annotation level, primarily by an AI agent (directly, or through a skill), not by a human stakeholder browsing for orientation.

`doc/concept/spring` documents the recurring implementation patterns of the Spring backend, including:

- REST controllers
- GraphQL controllers and queries
- GraphQL batch mappings
- Schema-first GraphQL
- Custom GraphQL scalars
- JPA entities and relations
- JPA repositories
- QueryDSL repositories
- Security configuration
- Transaction boundaries
- Jackson/JSON processing
- Database configuration
- Liquibase migrations
- Spring Boot auto-configuration
- Scheduling

`doc/concept/angular` and `doc/concept/svelte` document the equivalent per-framework patterns for each in-repo client's entity editor, lister, and service.

## How a topic guide is structured

- A top-level guide (e.g. `spring/endpoint.adoc`, `spring/database.adoc`, `spring/schedule.adoc`) documents one configuration area: its properties file, its `@Configuration` class, and — via an `== Implementation details` section — the finer-grained patterns that area relies on.
- A file whose name starts with `_` (e.g. `spring/_json-jpa-entity.adoc`, `spring/_json-jpa-rest-controller.adoc`, `spring/_querydsl-repository.adoc`, `spring/_graphql-controller.adoc`) is a partial: it is `include::`-d into one or more top-level guides (with `leveloffset`), never read as a document on its own, and never linked to as if it were one.
- A guide is built from real, included source — `include::` on the actual `.java`/`.xml`/`.properties` file, `[tags=...]` where only part of the file is relevant. Prose explains what an included fragment means; it does not restate its content.
- A guide's test-approach section (e.g. `_json-jpa-rest-controller.adoc`'s `== Tests`) documents the actual, current test pattern — ordering, fixtures, cleanup, snippet generation — including its accepted trade-offs. A guide describes the pattern actually used here, not an idealized alternative.

## How a skill uses these guides

A skill that implements or documents a backend or frontend artifact treats a `doc/concept` guide as its baseline, not as background reading:

- It names the exact guide to use as the implementation baseline for each artifact it produces — e.g. an entity class and its Liquibase changeset both cite `_json-jpa-entity.adoc`; a REST controller and its test both cite `_json-jpa-rest-controller.adoc`; a REST documentation skill cites `endpoint.adoc`.
- It lists the concrete artifacts to create or update, and how the result is validated afterward — a skill instruction is only complete once it states both what to produce and the tests that confirm it.
- An artifact that deviates from its cited guide's pattern is either a defect in the artifact or a sign the guide is stale — check the guide against the current code before assuming the artifact is wrong.

## Precedence

`doc/concept` sits between `doc/arc42` and `openspec` in the truth model (see `doc/arc42/AGENTS.md`): more detailed than the bird's-eye view, but still descriptive of today's implementation pattern, not a normative requirement — that is `openspec`'s role. A `doc/concept` guide that no longer matches the code it documents is stale and is corrected to follow the code, the same rule the truth model applies to every artifact below code and tests.

## Hard rule: documentation-only changes

Editing `doc/concept` is a documentation change. It never includes editing `.java`, `.js`, `.ts`, or Gradle files — those are separate, implementation-scoped changes; a `doc/concept` guide describes an existing pattern, it does not itself introduce one.
