---
name: Domain entity creator
description: |
  Creates a new entity in the domain model for the backend.
  Creates all implementation files needed to represent the entity.
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

When triggered, create a new entity with all its implementation files for the given entity name and properties.
Do not change existing entities.
Do not change client files.
Do not change documentation.

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target entity**
  Extract the entity name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Check that NO entity class with this name already exists.
  Replace placeholder `<Entity>` with the given name.
  Replace placeholder `<entity>` with lowercase name.

2. **Identify target domain package**
  Extract the domain package name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Check that a package directory with this name exists.
  Replace placeholder `<package>` with the given name.

3. **Locate implementation files**
  Locate existing implementation files:
  - **Entity fact sheets** → Check *.adoc in lib/backend-api/src/main/java/**/api/**
  - **Entity classes** → Check *.java in lib/backend-api/src/main/java/**/api/**
  - **Entity test classes** → Check *Test.java in lib/backend-api/src/test/java/**
  - **Liquibase scripts** → Check *.xml in lib/backend-data/src/main/resources/liquibase/v1
  - **Repository interfaces** → Check *Repository.java in lib/backend-data/src/main/java/**/app/**
  - **REST controller classes** → Check *RestController.java in lib/backend-data/src/main/java/**/app/**
  - **REST controller advice** → Check *RestControllerAdvice.java in lib/backend-data/src/main/java/**/app/**
  - **REST API test classes** → Check *RestApiTest.java in lib/backend-data/src/main/java/**/app/**
  - **GraphQL schema files** → Check *.gqls in lib/backend-data/src/main/resources/graphql
  - **GraphQL controller classes** → Check *GraphqlController.java in lib/backend-data/src/main/java/**/app/**
  - **GraphQL test classes** → Check *GraphqlTest.java in lib/backend-data/src/test/java/**/app/**
  - **Server test classes** → Check ServerRunnerTest.java in app/server/src/test/java/**/app/**

## Task steps

1. **Create entity fact sheet <Entity>.adoc**
  Add a short description of the main purpose of the entity.

2. **Create Liquibase script <entity>.xml**
  Use `doc/concept/spring/entity.adoc` as the implementation baseline.
  Create a new change set with:
  - A column with name `id` of type `UUID` (not null).
  - A column with name `version` of type `BIGINT` (not null).
  Add a `preCondition` element that checks `tableExists tableName="<entity>"`.
  Include the new file `<entity>.xml` in `changelog.xml` at the end of the list.

3. **Create entity class <Entity>.java**
  Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.
  Annotate with `@Entity`, `@Table(name = "<entity>")`.
  Implement package-visible constructors.
  Implement property `id` property following `_json-jpa-entity-id.adoc`.
  Implement property `version` property following `_json-jpa-entity-version.adoc`.
  Implement operation `isEqual`.
  Do not implement operation `verify` as it is used for individual changes.
  Implement operation `withId`.
  Do not implement operation `extraJson` as it is used for individual changes.

4. **Create entity test class <Entity>Test.java**
  Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.

5. **Create repository interface <Entity>Repository.java**
  Use `doc/concept/spring/_json-jpa-repository.adoc` as the implementation baseline.

6. **Create repository test class <Entity>RepositoryTest.java**
  Use `doc/concept/spring/_json-jpa-repository.adoc` as the implementation baseline.

7. **Create REST controller class <Entity>RestController.java**
  Use `doc/concept/spring/_json-jpa-rest-controller.adoc` as the implementation baseline.

8. **Create REST API test class <Entity>RestApiTest.java**
  Use `doc/concept/spring/_json-jpa-rest-controller.adoc` as the implementation baseline.

9. **Create GraphQL schema file <entity>.gqls**
  Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

10. **Create GraphQL controller class <Entity>GraphqlController.java**
  Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

11. **Create GraphQL test class <Entity>GraphqlTest.java**
  Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

## Task output

Create a short summary with files changed.
