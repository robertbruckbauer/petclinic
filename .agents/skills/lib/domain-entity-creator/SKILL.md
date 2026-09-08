---
name: domain-entity-creator
description: Create a new backend domain entity and all required artifacts; use for prompts like "Create an entity ... in package ...".
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target entity

Extract the entity name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Check that NO entity class with this name already exists.
Replace placeholder `{Entity}` with the given name.
Replace placeholder `{entity}` with kebab case of the the given name.
Replace placeholder '{table}' with snake case of the the given name.

### Identify target domain package

Extract the domain package name from the request.
Check if name is a valid identifier for the programming languages Java, Typescript and SQL.
Check that a package directory with this name exists.
Replace placeholder `{package}` with the given name.

## Task steps

### Create entity fact sheet {Entity}.adoc

Add a short description of the main purpose of the entity.

### Create Liquibase changeset {entity}.xml

Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.
Create a new change set with:
- A column with name `id` of type `UUID` (not null).
- A column with name `version` of type `BIGINT` (not null).
Add a `preCondition` element that checks `tableExists tableName="{entity}"`.
Include the new file `{entity}.xml` in `changelog.xml` at the end of the list.

### Create entity class {Entity}.java

Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.
Annotate with `@Entity`, `@Table(name = "{entity}")`.
Add package-visible constructors.
Add property `id` property following `_json-jpa-entity-version-id.adoc`.
Add property `version` property following `_json-jpa-entity-version-id.adoc`.
Add operation `isEqual`.
Add operation `withId`.
Add operation `verify` only when requested.
Add operation `extraJson` only when requested as it may have negative impact on the performance if relations are involved.

### Create entity test class {Entity}Test.java

Use `doc/concept/spring/_json-jpa-entity.adoc` as the implementation baseline.
Implement tests from the test approach example only.

### Create repository interface {Entity}Repository.java

Use `doc/concept/spring/_json-jpa-repository.adoc` as the implementation baseline.

### Create repository test {Entity}RepositoryTest.java

Use `doc/concept/spring/_json-jpa-repository.adoc` as the implementation baseline.
Implement tests from the test approach example only.

### Create REST API controller class {Entity}RestController.java

Use `doc/concept/spring/_json-jpa-rest-controller.adoc` as the implementation baseline.

### Create REST API test {Entity}RestApiTest.java

Use `doc/concept/spring/_json-jpa-rest-controller.adoc` as the implementation baseline.
Add `DELETE` statement for database table `{table}` in DatabaseCleaner class.
Add `getApi{Entity}NoElement` test.
Add `postApi{Entity}` test.
Add `postApi{Entity}Conflict` test if entity has unique columns.
Add `putApi{Entity}` test.
Add `patchApi{Entity}` tests for all properties.
Add `patchApi{Entity}` tests for all collections.
Add `patchApi{Entity}` tests for all relations.
Add `getApi{Entity}` test.
Add `getApi{Entity}ById` test.
Add `getApi{Entity}ByIdNotFound` test.
Add `delete{Entity}` test.
Add `delete{Entity}NotFound` test.

### Create GraphQL schema {Entity}.gqls

Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.
Add `all{Entity}: [{Entity}]` in `Query.gqls`. 
Add `{entity}ById(id: ID!): {Entity}` in `Query.gqls`.
Do not add other queries.   

### Create GraphQL controller class {Entity}GraphqlController.java

Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.

### Create GraphQL test {Entity}GraphqlTest.java

Use `doc/concept/spring/_graphql-controller.adoc` as the implementation baseline.
Implement tests from the test approach example only.
