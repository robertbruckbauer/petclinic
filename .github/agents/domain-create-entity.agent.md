---
name: Domain entity creator
description: |
  Creates a new entity in the domain model for the backend.
  Creates all implementation files needed to represent the entity.
---

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

## Task steps

1. **Create entity fact sheet <Entity>.adoc**
  Add a short description of the main purpose of the entity.

2. **Create Liquibase changeset <entity>.xml**
  Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.
  Create a new change set with:
  - A column with name `id` of type `UUID` (not null).
  - A column with name `version` of type `BIGINT` (not null).
  Add a `preCondition` element that checks `tableExists tableName="<entity>"`.
  Include the new file `<entity>.xml` in `changelog.xml` at the end of the list.

3. **Create entity class <Entity>.java**
  Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.
  Annotate with `@Entity`, `@Table(name = "<entity>")`.
  Add package-visible constructors.
  Add property `id` property following `_json-jpa-entity-id.adoc`.
  Add property `version` property following `_json-jpa-entity-version.adoc`.
  Add operation `isEqual`.
  Add operation `withId`.
  Add operation `verify` only when requested.
  Add operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.

4. **Create entity test class <Entity>Test.java**
  Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.

5. **Create repository interface <Entity>Repository.java**
  Use `doc/concept/spring/_json-jpa-repository.adoc` as the implementation baseline.

6. **Create repository test <Entity>RepositoryTest.java**
  Use `doc/concept/spring/_json-jpa-repository.adoc` as the implementation baseline.

7. **Create REST API controller class <Entity>RestController.java**
  Use `doc/concept/spring/_json-jpa-rest-controller.adoc` as the implementation baseline.

8. **Create REST API test <Entity>RestApiTest.java**
  Use `doc/concept/spring/_json-jpa-rest-controller.adoc` as the implementation baseline.
  Add `DELETE` statement for database table <entity> in DatabaseCleaner.java.

9. **Create GraphQL schema <entity>.gqls**
  Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

10. **Create GraphQL controller class <Entity>GraphqlController.java**
  Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

11. **Create GraphQL test <Entity>GraphqlTest.java**
  Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

## Task output

Create a short summary with files changed.
