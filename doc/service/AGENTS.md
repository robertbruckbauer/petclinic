# Agent instructions for `doc/service/`

## Role of this directory

`doc/service` is the technical reference for what the implementation actually provides — not for what it should provide. `openspec` describes the target state (the *Soll*): a technology-independent, normative contract. `doc/service` documents the actually implemented technical interface (the *Ist*): derived from the real REST/GraphQL code and its executed tests. It is not a second normative specification, and it never states a requirement — only what the current implementation and its test suite demonstrate.

Because it is mechanically derived from code and tests rather than authored as an independent claim, `doc/service` sits outside the five-artifact truth model in `doc/arc42/AGENTS.md`: it cannot "disagree" with code and tests the way `doc/arc42`/`doc/concept`/`openspec`/`obsidian` can, since it is a rendering of them. A `doc/service` file that no longer matches its source is stale documentation to regenerate, not a conflict to resolve by precedence.

## What feeds a REST API document

An `{entity}-restapi.adoc` file is derived from:

- Spring REST Docs snippets (`lib/backend-data/build/generated-snippets`, via `{restdocdir}`).
- Executed controller and component tests — a documented operation must be backed by a passing test that produced its snippet; an operation with no snippet gets a `TIP` requesting the test, not an invented example.
- The actual HTTP requests and responses those tests captured.
- Entity classes, repository interfaces, and controller classes (names, properties, methods).
- Annotations (`@Column`, `@ElementCollection`, `@ManyToOne`/`@OneToMany`/`@ManyToMany`, and similar).
- Database and mapping information (the Liquibase changeset for the aggregate root's table).

This is what lets the document describe real requests, responses, status codes, error cases, filtering, sorting, and pagination — not a description of intent.

## What feeds a GraphQL API document

An `{entity}-graphql.adoc` file is derived from:

- The GraphQL schema (`lib/backend-data/src/main/resources/graphql`, via `{graphqldir}`).
- Controller classes, repository interfaces, and their annotations.
- The schema's queries and their arguments.
- Types, relations, and nullability as declared in the schema.
- Custom scalars and enums — each mentioned individually; built-in scalar types are not.
- `@BatchMapping` batch loading, where a relation uses it.

## How a document is produced or updated

- Every `{entity}-restapi.adoc`/`{entity}-graphql.adoc` follows `template/spring-restapi.adoc`/`template/spring-graphql.adoc` respectively — read the template before creating or regenerating a document; it is the authoritative, detailed generation prompt, not just an example.
- A code change that touches a documented controller, entity, repository, schema, or test is a reason to regenerate the affected `doc/service` file — this documentation drifting from the implementation it claims to describe is itself a defect.
- Included code/snippet fragments carry the documentation; prose does not repeat what an included fragment already shows.
- Don't add or change a level-1 section — the template fixes the document's top-level structure.
- A `TIP` admonition notes a deviation from the standard pattern (e.g. a missing test, an attribute documented once "with others following the same pattern"); never change an existing `TIP`.
- A `NOTE` admonition flags a prompt for improving the documentation itself (e.g. a schema/implementation mismatch worth calling out); update an existing `NOTE` when the underlying code changes it describes.

## Hard rule: documentation-only changes

Editing `doc/service` is a documentation change. It never includes editing `.java`, `.js`, `.ts`, or Gradle files — those are separate, implementation-scoped changes; a `doc/service` update reports what such a change did, it does not perform it.
