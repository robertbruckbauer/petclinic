---
name: domain-entity-collection-adder
description: Add a collection field to an existing backend entity and update dependent artifacts; use for prompts like "Add collection ... to entity ...".
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target entity

Extract the entity name from the request.
Check if the entity class exists.
Replace placeholder '{Entity}' with the given name.
Replace placeholder '{entity}' with lowercase of the the given name.
Replace placeholder '{table}' with snake case of the the given name.

### Identify collection name

Extract the collection element column name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Replace placeholder '{Name}' with the given name.
Replace placeholder '{name}' with camel case of the the given name.
Replace placeholder '{column}' with snake case of the the given name.

### Identify element type

Extract the element type from the request.
Check if implementation guide doc/concept/spring/_json-jpa-entity-collection-{type}.adoc exists.
Replace placeholder '{Type}' with the given type.
Replace placeholder '{type}' with camel case of the the given type.

## Task steps

### Determine collection semantics

Extract optionality, ordering and fetch strategy from the request.
If not specified, use defaults from the implementation guide.

### Update entity fact sheet {Entity}.adoc

Add a short description for the new collection with its type, constraints, and a one-line description.

### Create Liquibase changeset {table}_{column}.xml

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Create join table `{table}_{column}` with:
- A column with name `id` of type `UUID` (not null) referencing the parent entity's primary key.
- A column with name `{column}` with the Liquibase type for `Type` (not null).
Add the foreign key constraint `fk_{table}_{column}_id` from `{table}_{column}.id` to `{table}.id`.
Add a `preCondition` element that checks `tableExists tableName="{table}_{column}"`.
Include the new script `{table}_{column}.xml` in `changelog.xml` directly after the existing file `{table}.xml`.

### Update entity class {Entity}.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add property with name `all{Name}` of type `Type` and annotations.
Update constructor initialization.
Update operation `isEqual`.
Update operation `withId`.
Add or update operation `verify` only when requested.
Add or update operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.
Add operation`addAll{Name}`.

### Update entity test class {Entity}Test.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If collection is mandatory update existing test data with a default value.
Update existing tests with asserts for the new collection.
Add `json{Name}` test.
Add `json{Name}Contraints` test if collection has constraints.

### Update repository interface {Entity}Repository.java

No changes.

### Update repository test {Entity}RepositoryTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If collection is mandatory update existing test data with a default value.
Update existing tests with asserts for the new collection.
Do not create a new test for the relation.

### Update REST API controller class {Entity}RestController.java

No changes.

### Update REST API test {Entity}RestApiTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add `DELETE` statement for database table `{table}_{name}` in DatabaseCleaner class.
If collection is mandatory update existing test data with a default value.
Update existing tests with asserts for the new collection.
Add `patchApi{Entity}{Name}` test.

### Update GraphQL schema {Entity}.gqls

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add `all{Name}: [{Type}]` to GraphQL type `{Entity}` in `{Entity}.gqls` with correct nullability.
Do not add new queries in `Query.gqls`.

### Update GraphQL controller class {Entity}GraphqlController.java

No changes.

### Update GraphQL test {Entity}GraphqlTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If collection is mandatory update existing test data with a default value.
Update existing tests with asserts for the new collection.
Do not create a new test for the collection.

### Update Server test set

If collection is mandatory update existing test data with a default value.
