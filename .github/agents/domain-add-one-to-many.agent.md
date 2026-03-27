---
name: Domain entity one-to-many relation adder
description: |
  Adds a new one-to-many relation from one existing entity to another entity of the domain model for the backend.
  Extends existing REST endpoints and GraphQL operations for the entities.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target entity**
  Extract the entity name from the request.
  Check if the entity class exists.
  Replace placeholder '<Entity>' with the given name.
  Replace placeholder '<entity>' with lowercase name.

2. **Identify relation name**
  Extract the relation column name from the request.
  Check if each name is a valid identifier for the programming languages Java, Typescript and SQL.
  Replace placeholder '<Name>' with the capitalized name.
  Replace placeholder '<name>' with lowercase name.

3. **Identify relation types**
  Extract the relation type from the request.
  Check if implementation guide doc/concept/spring/_json-jpa-entity-collection-<type>.adoc exists.
  Replace placeholder '<Type>' with the given type.
  Replace placeholder '<type>' with lowercase type.

## Task steps

1. **Determine relation semantics**
  Extract optionality and fetch strategy from the request.
  If not specified, use defaults from the implementation guide.

2. **Update entity fact sheet <Entity>.adoc**
  Add a short description for the new relation with its type, constraints, and a one-line description.

3. **Do not update Liquibase changesets**
  
4. **Update entity class <Entity>.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Add property with name `all<Type>` of type `Type` and annotations.
  Initialize `all<Type>` in both constructors.
  Update operation `isEqual`.
  Update operation `withId`.
  Add or update operation `verify` only when requested.
  Add or update operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.
  Add operation `addAll<Type>`.
  Keep style consistent with existing entity patterns.

5. **Update entity test class <Entity>Test.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If relation is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new relation.
  Keep style consistent with existing tests.

6. **Do not update repository interface <Entity>Repository.java**

7. **Update repository test <Entity>RepositoryTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If relation is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new relation.
  Keep style consistent with existing tests.

8. **Do not update REST API controller class <Entity>RestController.java**

9. **Update REST API test <Entity>RestApiTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If relation is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new relation.
  Keep style consistent with existing tests.

10. **Update GraphQL controller class <Entity>GraphqlController.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Add `all<Type>` of GraphQL type for `[Type]` to GraphQL type with name`<Entity>` in <entity>.gqls with correct nullability.

11. **Update GraphQL test <Entity>GraphqlTest.java**
  Update tests for new relation as required in the implementation guide.
  If relation is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new relation.
  Keep style consistent with existing tests.

12. **Update Server test set**
  If relation is mandatory update existing test data with a default value.
  
## Task output

Create a short summary with files changed.
