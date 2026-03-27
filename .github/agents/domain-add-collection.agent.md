---
name: Domain entity collection adder
description: |
  Adds a new collection to an existing entity of the domain model for the backend.
  Extends existing REST endpoints and GraphQL operations for the entity.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target entity**
  Extract the entity name from the request.
  Check if the entity class exists.
  Replace placeholder '<Entity>' with the given name.
  Replace placeholder '<entity>' with lowercase name.

2. **Identify collection name**
  Extract the collection element column name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Replace placeholder '<Name>' with the capitalized name.
  Replace placeholder '<name>' with lowercase name.
  The class property name is `all<Name>`.
  The join table name is `<entity>_<name>`.

3. **Identify element type**
  Extract the element type from the request.
  Check if implementation guide doc/concept/spring/_json-jpa-entity-collection-<type>.adoc exists.
  Replace placeholder '<Type>' with the given type.
  Replace placeholder '<type>' with lowercase type.

## Task steps

1. **Determine collection semantics**
  Extract optionality, ordering and fetch strategy from the request.
  If not specified, use defaults from the implementation guide.

2. **Update entity fact sheet <Entity>.adoc**
  Add a short description for the new collection with its type, constraints, and a one-line description.

3. **Create Liquibase script <entity>_<name>.xml**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Create join table `<entity>_<name>` with:
  - A column with name `id` of type `UUID` (not null) referencing the parent entity's primary key.
  - A column with name `<name>` with the Liquibase type for `Type` (not null).
  Add the foreign key constraint `fk_<entity>_<name>_id` from `<entity>_<name>.id` to `<entity>.id`.
  Add a `preCondition` element that checks `tableExists tableName="<entity>_<name>"`.
  Include the new script `<entity>_<name>.xml` in `changelog.xml` directly after the existing file <entity>.xml.

4. **Update entity class <Entity>.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Add property with name `all<Name>` of type `Type` and annotations.
  Update constructor initialization.
  Update operation `isEqual`.
  Update operation `withId`.
  Add or update operation `verify` only when requested.
  Add or update operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.
  Add operation`addAll<Name>`.
  Keep style consistent with existing entity patterns.

5. **Update entity test class <Entity>Test.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If collection is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new collection.
  Add `json<Name>` test.
  Add `json<Name>Contraints` test if collection has constraints.
  Keep style consistent with existing tests.

6. **Do not update repository interface <Entity>Repository.java**

7. **Update repository test class <Entity>RepositoryTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If collection is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new collection.
  Keep style consistent with existing tests.

8. **Do not update REST controller class <Entity>RestController.java**

9. **Update REST API test class <Entity>RestApiTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If collection is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new collection.
  Add `patchApi<Entity><Name>` test.
  Keep style consistent with existing tests.

10. **Update GraphQL controller class <Entity>GraphqlController.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Add `all<Name>` of GraphQL type for `[Type]` to GraphQL type with name`<Entity>` in <entity>.gqls with correct nullability.

11. **Update GraphQL test class <Entity>GraphqlTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If collection is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new collection.
  Keep style consistent with existing tests.

12. **Update Server test class**
  If collection is mandatory update existing test data with a default value.

## Task output

Create a short summary with files changed.
