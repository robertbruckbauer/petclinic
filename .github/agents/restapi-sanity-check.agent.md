---
name: REST API sanity checker
description: |
  Performs a comprehensive sanity check of REST API documentation files.
  Validates structure, content accuracy and alignment with implementation.
---

## Task steps

1. **Map implementation files to documentation files**
  For each changed implementation file, identify which REST API documentation files may need updates:
  - **Entity classes** → corresponding *-restapi.adoc Model section
  - **Liquibase scripts** → corresponding *-restapi.adoc Model section
  - **Repository interfaces** → corresponding *-restapi.adoc query parameter documentation
  - **RestController classes** → corresponding *-restapi.adoc Operations section
  - **Rest API Test classes** → corresponding *-restapi.adoc usage examples and scenarios
  - **ControllerAdvice classes** → all *-restapi.adoc error code documentation

2. **Verify documentation files**
  Use doc/service/template/spring-restapi.adoc as the reference template.
  - Strictly follow the template's structure and rules
  - Ensure all required sections are present and properly formatted
  - Fix any typos or grammatical errors
  - Verify AsciiDoc syntax correctness
  - Check for consistent formatting and style

3. **Verify implementation files alignment**
  Use doc/concept/spring/endpoint.adoc as the implementation baseline.
  - Compare documentation descriptions with actual implementation changes
  - Verify new/modified endpoints are documented
  - Validate parameter descriptions against updated method signatures
  - Check response models against modified entity definitions
  - Ensure error codes align with ControllerAdvice changes

4. **Assess impact**
    For minor issues (typos, formatting): Fix directly
    For major issues (missing documentation for new features, outdated descriptions): 
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

- [ ] All changed REST endpoints are documented
- [ ] New HTTP methods and mappings are included
- [ ] Modified request/response schemas are accurate
- [ ] Updated error codes and messages are documented
- [ ] New validation constraints are reflected
- [ ] Changed entity properties are documented
- [ ] New query parameters from repository changes are included
- [ ] Cross-references are working for modified files
- [ ] AsciiDoc syntax is correct

## Task output

For each affected documentation file, provide:
1. **Implementation changes detected**: List specific changes in related implementation files
2. **Documentation impact**: List documentation sections that needed updates
3. **NOTE admonitions added**: List new admonitions with guidance for manual review
