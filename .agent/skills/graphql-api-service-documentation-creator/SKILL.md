---
name: graphql-api-service-documentation-creator
description: Create or update GraphQL API service documentation from implementation files; use for prompts like "Update GraphQL API documentation" or "Create GraphQL API documentation for ...".
---


## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target entity

Extract the entity name from the request.
Check if the entity class exists.
Replace placeholder '{Entity}' with the given name.
Replace placeholder '{entity}' with kebab case of the the given name.

## Task steps

### Analyse implementation files

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
- **Entity class analysis**:
  * Extract properties, data types, and validation annotations
  * Document relationships and constraints
  * Identify required vs optional fields
- **Liquibase changeset analysis**:
  * Extract table structure and column definitions
  * Document constraints, indexes, and relationships
- **Repository analysis**:
  * Document custom query methods and their parameters
  * Identify finder methods and their purposes
- **GraphQL schema analysis**:
  * Extract type definitions and their fields
  * Document custom scalar types and their Java mappings
  * Identify relationships between types
  * Document query and mutation operation signatures
- **GraphqlController analysis**:
  * Extract all query and mutation operations
  * Document @QueryMapping and @MutationMapping methods
  * Identify @BatchMapping methods for relationship loading
  * Document arguments and return types
  * Identify optimization strategies for n+1 query problems
- **GraphqlTest Analysis**:
  * Identify test scenarios and expected behaviors
  * Extract example queries and responses
  * Document edge cases and error conditions

### Analyse GraphQL service template

Use doc/service/template/spring-graphql.adoc as the reference template.
- Understand the required sections and their structure
- Understand formatting rules and conventions

### Generate GraphQL service documentation

Create or update {entity}-graphql.adoc file in the doc/service/ directory.
- Strictly follow the template's formatting rules and conventions
- Maintain consistent style with existing documentation
- Do not include instructions from the template
- Use the include directive to reference GraphQL schema definition files
- Document batch loading strategies where applicable
- Document custom scalar types and their Java mappings

## Validation checklist
- [ ] All GraphQL types are documented
- [ ] Query operations are documented with arguments and return types
- [ ] Mutation operations are documented (if applicable)
- [ ] Batch mapping optimizations are explained
- [ ] Custom scalar types are documented
- [ ] Entity properties are documented
- [ ] Schema includes are correctly referenced
- [ ] Relationships to domain model are clear
- [ ] Cross-references are working
- [ ] AsciiDoc syntax is correct

## Task output

Create a short summary of identified GraphQL types and query operations.
