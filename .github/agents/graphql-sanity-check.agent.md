---
name: GraphQL API sanity checker
description: |
  Performs a comprehensive sanity check of GraphQL API documentation files.
  Validates structure, content accuracy and alignment with implementation.
---

## Task steps

### Map implementation files to documentation files

For each changed implementation file, identify which GraphQL API documentation files may need updates:
- **Entity class** → GraphQL API documentation, model section
- **Liquibase changeset** → GraphQL API documentation, model section
- **Repository interface** → GraphQL API documentation, query parameter documentation
- **GraphQL schema** → GraphQL API documentation, operations and type definitions sections
- **GraphQL controller class** → GraphQL API documentation, query resolver documentation
- **GraphQL test** → GraphQL API documentation, usage examples and scenarios

### Verify documentation files

Use doc/service/template/spring-graphql.adoc as the reference template.
- Strictly follow the template's structure and rules
- Ensure all required sections are present and properly formatted
- Fix any typos or grammatical errors
- Verify AsciiDoc syntax correctness
- Check for consistent formatting and style

### Verify implementation files alignment

  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  - Compare documentation descriptions with actual implementation changes
  - Verify new/modified queries and mutations are documented
  - Validate parameter descriptions against updated resolver method signatures
  - Check response models against modified entity definitions and GraphQL schemas
  - Ensure type definitions in documentation match schema files (.gqls)
  - Verify resolver documentation aligns with controller implementations

### Assess impact

  For minor issues (typos, formatting): Fix directly
  For major issues (missing documentation for new features, wrong or outdated descriptions): 
  - Add NOTE admonitions with specific improvement instructions
  - Reference the specific implementation changes that require documentation updates
  - Write instructions as actionable prompts for fixing
  - Update existing NOTE admonitions if they address the same issue
  - Only add NOTE admonitions when changes are actually recommended

## Rules for NOTE admonitions

- Use this format: `NOTE: [Specific instruction for improvement]`
- Make instructions actionable and specific
- Include file references where applicable
- Provide clear steps for resolution
- Don't add redundant notes for the same issue

## Validation checklist

- [ ] All changed GraphQL queries and mutations are documented
- [ ] New type definitions and field modifications are included
- [ ] Modified request/response schemas match .gqls schema files
- [ ] Updated resolver implementations are documented
- [ ] New validation constraints are reflected
- [ ] Changed entity properties are documented in GraphQL context
- [ ] New query parameters from repository changes are included
- [ ] GraphQL schema evolution is properly documented
- [ ] Cross-references are working for modified files
- [ ] AsciiDoc syntax is correct
- [ ] Usage examples reflect current API capabilities

## Task output

For each affected documentation file, provide:
1. **Implementation changes detected**: List specific changes in related implementation files
2. **Documentation impact**: List documentation sections that needed updates
3. **NOTE admonitions added**: List new admonitions with guidance for manual review
