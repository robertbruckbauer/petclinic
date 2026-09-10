# Agent instructions for `doc/service/`

## Role of this directory

`doc/service/` contains the technical reference for what the implementation actually provides — not for what it should provide. It is not a second normative specification, and it never states a requirement — only what the current implementation and its test suite demonstrate.

Because it is mechanically derived from code and tests rather than authored as an independent claim, `doc/service/` sits outside the five-artifact truth model: it cannot "disagree" with code and tests, since it is a rendering of them. A file in `doc/service/` that no longer matches its source is stale documentation to regenerate, not a conflict to resolve by precedence.

## What feeds a REST API document

An `{entity}-restapi.adoc` file is derived from:

- Asciidoc snippets (`lib/backend-data/build/generated-snippets`, via `{restdocdir}`), produced by a passing controller and component tests.
- Entity classes, repository interfaces, and controller classes, and their annotations.
- Liquibase changeset for the aggregate root's table and sibling tables.

This is what lets the document describe real requests, responses, status codes, error cases, filtering, sorting, and pagination — not a description of intent.

## What feeds a GraphQL API document

An `{entity}-graphql.adoc` file is derived from:

- The GraphQL schema (`lib/backend-data/src/main/resources/graphql`, via `{graphqldir}`).
- Entity classes, repository interfaces, and controller classes, and their annotations.
- Custom scalars and enums — each mentioned individually; built-in scalar types are not.

## How a document is produced or updated

Editing `doc/service/` is a documentation change by AI agents through its maintenance skills — never by direct human edit.

