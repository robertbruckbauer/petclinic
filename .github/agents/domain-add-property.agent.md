---
name: Domain entity property adder
description: |
  Adds a new property to an existing entity of the domain model for the backend.
  Extends existing REST endpoints and GraphQL operations for the entity.
---

## Your role

You are a full-stack developer with T-shaped skills.
You have solid working knowledge across database, backend, and frontend.
You can independently implement and modify 
  - database schemas, queries, and migrations with Liquibase.
  - backend data model, GraphQL operations and business logic.
  - frontend templates, components and services.
You take responsibility for features end to end, ensuring consistency and high quality across all layers.
You create clean code within boundaries set by existing concepts.
  
## Task overview

When triggered, implement a new property for requested type in the given entity.
Do not add custom REST endpoints.
Do not add new GraphQL operations.
Do not change documentation.
Do not change client files.

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target entity**
  Extract the entity name from the request.
  Check if the entity class exists.
  Replace placeholder '<Entity>' with the given name.
  Replace placeholder '<entity>' with lowercase name.

2. **Identify property name**
  Extract the property name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Replace placeholder '<Name>' with the given name.
  Replace placeholder '<name>' with lowercase name.

3. **Identify property type**
  Extract the property type from the request.
  For an enum type, check if the implementation guide doc/concept/spring/_json-jpa-entity-column-enum.adoc exists.
  For other types, check if the implementation guide doc/concept/spring/_json-jpa-entity-column-<type>.adoc exists.
  Replace placeholder '<Type>' with the given type.
  Replace placeholder '<type>' with lowercase type.

4. **Locate implementation files**
  Analyze and update implementation files:
  - **Entity fact sheets** → Check *.adoc in lib/backend-api/src/main/java/**/api/**
  - **Entity classes** → Check *.java in lib/backend-api/src/main/java/**/api/**
  - **Entity test classes** → Check *Test.java in lib/backend-api/src/test/java/**
  - **Liquibase scripts** → Check *.xml in lib/backend-data/src/main/resources/liquibase/v1
  - **Repository classes** → Check *Repository.java in lib/backend-data/src/main/java/**/app/**
  - **REST controller classes** → Check *RestController.java in lib/backend-data/src/main/java/**/app/**
  - **REST controller advice** → Check *RestControllerAdvice.java in lib/backend-data/src/main/java/**/app/**
  - **GraphQL schema files** → Check *.gqls in lib/backend-data/src/main/resources/graphql
  - **GraphQL controller classes** → Check *GraphqlController.java in lib/backend-data/src/main/java/**/app/**
  - **GraphQL test classes** → Check *GraphqlTest.java in lib/backend-data/src/test/java/**/app/**
  - **Server test classes** → Check *GraphqlTest.java in app/server/src/test/java/**/app/**

## Task steps

1. **Determine property semantics**
  Extract optionality and constraints from the requested.
  If not specified, use defaults from the implementation guide.

2. **Implement Liquibase change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  In <entity>.xml, create a new change set without any preconditions.
  Add a new column with name `<name>`.
  Match nullability, default values and constraints to the implementation guide.
  Add uniqueness constraints only if requested.

3. **Implement <Entity>.java change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  Add property with name `<name>` of type `Type` and annotations.
  Update constructor initialization.
  Update operations:
  - `isEqual`
  - `verify`
  - `withId`
  Add operations:
  - `extraJson` (only when requested)
  - `set<Name>` (only when requested)
  Keep style consistent with existing entity patterns.

4. **Implement <Entity>Test.java change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.
  Add `json<Name>` test.
  Add `json<Name>Contraints` test if property has constraints.
  Keep style consistent with existing tests.

5. **Implement <Entity>RepositoryTest.java change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.
  Keep style consistent with existing tests.

6. **No REST <Entity>RestController.java change**
    
7. **Implement <Entity>RestApiTest.java change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.
  Add `patchApi<Entity><Name>` test.
  Keep style consistent with existing tests.

8. **Implement <Entity>GraphqlController.java change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  Add <name> of GraphQL type for `Type` to GraphQL type with name`<Entity>` in <entity>.gqls with correct nullability.

9. **Implement <Entity>GraphqlTest.java change**
  Use doc/concept/spring/entity.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.
  Keep style consistent with existing tests.

10. **Implement Server test change**
  If property is mandatory update existing payloads with a default value.

## Task output

Create a short summary with files changed.
