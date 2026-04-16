---
name: Domain entity many-to-one relation adder
description: |
  Adds a new many-to-one relation from one existing entity to another entity of the domain model for the backend.
  Extends existing REST endpoints and GraphQL operations for the entities.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target entity

Extract the entity name from the request.
Check if the entity class exists.
Replace placeholder '{Entity}' with the given name.
Replace placeholder '{entity}' with kebab case of the the given name.
Replace placeholder '{table}' with snake case of the the given name.

### Identify relation name

Extract the relation column name from the request.
Check if each name is a valid identifier for the programming languages Java, Typescript and SQL.
Replace placeholder '{Name}' with the given name.
Replace placeholder '{name}' with camel case of the the given name.
Replace placeholder '{column}' with snake case of the the given name.

### Identify relation type

Extract the relation type from the request.
Check if the entity class exists.
Replace placeholder '{Type}' with the given type.
Replace placeholder '{type}' with camel case of the the given type.
Replace placeholder '{otherTable}' with snake case of the the given type.

## Task steps

### Determine relation semantics

Extract optionality and fetch strategy from the request.
If not specified, use defaults from the implementation guide.

### Update entity fact sheet {Entity}.adoc

Add a short description for the new relation with its type, constraints, and a one-line description.

### Update Liquibase changeset {table}.xml

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Create a new change set without any preconditions.
Add a new column with name `{column}_id`.
Match nullability, default values and constraints to the type guide.
Add the foreign key constraint `fk_{table}_{column}_id` from `{table}.{column}_id` to `{otherTable}.id`.

### Update entity class {Entity}.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add property with name `{name}` of type `Type`.
Add annotations.
Update constructor initialization.
Update operation `isEqual`.
Update operation `withId`.
Add or update operation `verify` only when requested.
Add or update operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.
Add operation `set{Name}` only when requested.

### Update entity test class {Entity}Test.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If relation is mandatory update existing test data with a default value.
Update existing tests with asserts for the new relation.

### Update repository interface {Entity}Repository.java

No changes.

### Update repository test {Entity}RepositoryTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If relation is mandatory update existing test data with a default value.
Update existing tests with asserts for the new relation.
  
### Update REST API controller class {Entity}RestController.java

No changes.

### Update REST API test {Entity}RestApiTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If relation is mandatory update existing test data with a default value.
Update existing tests with asserts for the new relation.
Add `patchApi{Entity}{Name}` test for the PATCH operation of the relation.
Add `getApi{Entity}{Name}` test for the GET operation of the relation.

### Update GraphQL schema {Entity}gqls

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add `{name}: {Type}` to GraphQL type `{Entity}` in {entity}.gqls with correct nullability.

### Update GraphQL controller class {Entity}GraphqlController.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add method `all{Name}` for batch loading.

### Update GraphQL test {Entity}GraphqlTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If relation is mandatory update existing test data with a default value.
Update existing tests with asserts for the new relation.

### Update Server test set

If relation is mandatory update existing test data with a default value.
