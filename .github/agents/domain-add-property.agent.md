---
name: Domain entity property adder
description: |
  Adds a new property to an existing entity of the domain model for the backend.
  Extends existing REST endpoints and GraphQL operations for the entity.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

1. **Identify target entity**
  Extract the entity name from the request.
  Check if the entity class exists.
  Replace placeholder '<Entity>' with the given name.
  Replace placeholder '<entity>' with kebab case of the the given name.
  Replace placeholder '<table>' with snake case nof the the given nameame.

2. **Identify property name**
  Extract the property name from the request.
  Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
  Replace placeholder '<Name>' with the given name.
  Replace placeholder '<name>' with camel case of the the given name.
  Replace placeholder '<column>' with snake case of the the given name.

3. **Identify property type**
  Extract the property type from the request.
  For an enum type, check if the implementation guide doc/concept/spring/_json-jpa-entity-column-enum.adoc exists.
  For other types, check if the implementation guide doc/concept/spring/_json-jpa-entity-column-<type>.adoc exists.
  Replace placeholder '<Type>' with the given type.
  Replace placeholder '<type>' with camel case of the the given type.

## Task steps

1. **Determine property semantics**
  Extract optionality and constraints from the requested.
  If not specified, use defaults from the implementation guide.

2. **Update entity fact sheet <Entity>.adoc**
  Add a short description for the new property with its type, constraints, and a one-line description.

3. **Update Liquibase changeset <table>.xml**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Create a new change set without any preconditions.
  Add a new column with name `<column>`.
  Match nullability, default values and constraints to the implementation guide.
  Add uniqueness constraints only if requested.

4. **Update entity class <Entity>.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Add property with name `<name>` of type `<Type>` and annotations.
  Update constructor initialization.
  Update operation `isEqual`.
  Update operation `withId`.
  Add or update operation `verify` only when requested.
  Add or update operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.
  Add operation `set<Name>` only when requested.

5. **Update entity test class <Entity>Test.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.
  Add `json<Name>` test.
  Add `json<Name>Contraints` test if property has constraints.

6. **Do not update repository interface <Entity>Repository.java**

7. **Update repository test <Entity>RepositoryTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.

8. **Do not update REST API controller class <Entity>RestController.java**
    
9. **Update REST API test <Entity>RestApiTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.
  Add `patchApi<Entity><Name>` test.

10. **Update GraphQL schema <Entity>gqls**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  Add <name> of GraphQL type for `Type` to GraphQL type with name`<Entity>` in <entity>.gqls with correct nullability.

11. **Do not update GraphQL controller class <Entity>GraphqlController.java**

12. **Update GraphQL test <Entity>GraphqlTest.java**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  If property is mandatory update existing test data with a default value.
  Update existing tests with asserts for the new property.

13. **Update Server test set**
  If property is mandatory update existing payloads with a default value.

## Task output

Create a short summary with files changed.
