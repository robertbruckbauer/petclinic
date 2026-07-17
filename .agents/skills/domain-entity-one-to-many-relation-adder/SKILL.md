---
name: domain-entity-one-to-many-relation-adder
description: Add a one-to-many relation between existing entities and update dependent artifacts; use for prompts like "Add a one-to-many relation ...".
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

### Identify relation on the other side

Check if the identified relation type has a `@ManyToOne` relation for this entity.

## Task steps

### Determine relation semantics

Extract optionality and fetch strategy from the request.
If not specified, use defaults from the implementation guide.

### Update entity fact sheet {Entity}.adoc

Add a short description for the new relation with its type, constraints, and a one-line description.

### Update Liquibase changesets

No changes.

### Update entity class {Entity}.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add property with name `all{Name}` of type `Type` and annotations.
Initialize `all{Name}` in both constructors.
Update operation `isEqual`.
Update operation `withId`.
Add or update operation `verify` only when requested.
Add or update operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.
Add operation `addAll{Name}`.

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
Do not create a new test for the relation.

### Update REST API controller class {Entity}RestController.java

No changes.

### Update REST API test {Entity}RestApiTest.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
If relation is mandatory update existing test data with a default value.
Update existing tests with asserts for the new relation.

### Update GraphQL schema {Entity}.gqls

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add `all{Name}: [{Type}]` to GraphQL type `{Entity}` in `{Entity}.gqls` with correct nullability.
Do not add new queries in `Query.gqls`.

### Update GraphQL controller class {Entity}GraphqlController.java

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
Add method `all{Name}` for batch loading.

### Update GraphQL test {Entity}GraphqlTest.java

Update tests for new relation as required in the implementation guide.
If relation is mandatory update existing test data with a default value.
Update existing tests with asserts for the new relation.
Do not create a new test for the relation.

### Update Server test set

No changes.
