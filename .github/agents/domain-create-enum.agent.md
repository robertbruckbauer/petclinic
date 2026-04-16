---
name: Domain enum creator
description: |
  Creates a new enum in the domain model for the backend.
  Creates all implementation files needed to represent the enum.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target enum

Extract the enum name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Check that NO enum class with this name already exists.
Replace placeholder `<Enum>` with the given name.

### Identify target domain package

Extract the domain package name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Check that a package directory with this name exists.
Replace placeholder `<package>` with the given name.

## Task steps

### Create enum fact sheet <Enum>.adoc

Add a short description of the main purpose of the entity.

### Create enum class <Enum>.java

Annotate with `@RequiredArgsConstructor`.
Implement values with a code derived from the text; use the first letter if they are unique.
Implement property `text` with type `String`.

### Create enum test class <Enum>Test.java

Implement a test which verifies the text.

### Create GraphQL schema <Enum>.gqls

Add a GraphQL type for the enum.
