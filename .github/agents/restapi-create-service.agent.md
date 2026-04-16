---
name: REST API service documentation creator
description: |
  Creates comprehensive REST API documentation for new REST endpoints based on implementation files.
  Uses template structure and generates documentation aligned with existing implementation.
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
- **RestController analysis**:
  * Extract all HTTP endpoints and mappings
  * Document request/response models
  * Identify path variables, query parameters, and request bodies
  * Document HTTP status codes and error responses
- **RestApiTest Analysis**:
  * Identify test scenarios and expected behaviors
  * Extract example requests and responses
  * Document edge cases and error conditions

### Analyse REST API service template

Use doc/service/template/spring-restapi.adoc as the reference template.
- Understand the required sections and their structure
- Understand formatting rules and conventions

### Generate REST API service documentation

Create or update a {entity}-restapi.adoc file in the doc/service/ directory.
- Strictly follow the template's formatting rules and conventions
- Maintain consistent style with existing documentation
- Do not include instructions from the template

## Validation checklist
- [ ] All REST endpoints are documented
- [ ] Request/response schemas are accurate
- [ ] Error codes and messages are documented
- [ ] Validation constraints are reflected
- [ ] Entity properties are documented
- [ ] Query parameters from repository are documented
- [ ] Cross-references are working
- [ ] AsciiDoc syntax is correct

## Task output

Create a short summary of identified REST endpoints.
