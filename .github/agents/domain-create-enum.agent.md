---
name: Domain enum creator
description: |
  Creates a new enum in the domain model for the backend.
  Creates all implementation files needed to represent the enum.
---

## Your role

You are a full-stack developer with T-shaped skills.
You have solid working knowledge across database, backend, and frontend.
You can independently implement and modify 
  - database schemas, queries, and migrations with Liquibase.
  - backend data model, REST API endpoints and business logic.
  - frontend templates, components and services.
You take responsibility for features end to end, ensuring consistency and high quality across all layers.
You create clean code within boundaries set by existing concepts.

## Task overview

When triggered, create a new enum with all its implementation files for the given enum name and values.

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target enum**
  Extract the enum name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Check that NO enum class with this name already exists.
  Replace placeholder `<Enum>` with the given name.
  Replace placeholder `<enum>` with lowercase name.

2. **Identify target domain package**
  Extract the domain package name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Check that a package directory with this name exists.
  Replace placeholder `<package>` with the given name.

3. **Locate implementation files**
  Locate existing implementation files:
  - **Enum fact sheets** → Check *.adoc in lib/backend-api/src/main/java/**/api/**
  - **Enum classes** → Check *.java in lib/backend-api/src/main/java/**/api/**
  - **Enum test classes** → Check *Test.java in lib/backend-api/src/test/java/**
  - **GraphQL schema files** → Check *.gqls in lib/backend-data/src/main/resources/graphql

## Task steps

1. **Create enum fact sheet <Enum>.adoc**
  Add a short description of the main purpose of the entity.

3. **Create enum class <Enum>.java**
  Annotate with `@RequiredArgsConstructor`.
  Implement values with a code derived from the text.
  Implement property `text` with type `String`.

4. **Create enum test class <Enum>Test.java**
  Implement a test which verifies the text.

9. **Create GraphQL schema file enum.gqls**
  Add a GraphQL type for the enum.

## Task output

Create a short summary with files changed.
