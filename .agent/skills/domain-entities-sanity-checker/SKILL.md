---
name: domain-entities-sanity-checker
description: Sanity-check domain entities and related backend artifacts; use for prompts like "Check domain entities" or targeted sanity updates such as adding missing entity tests.
---


## Task steps

### Verify fact sheets

Compare entity fact sheets with entity implementation.

### Verify Liquibase changes

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
- Check database models against entity definitions

### Verify domain entity changes

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
- Check coverage with tests

### Verify REST endpoints changes

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
- Check response models against entity definitions
- Check coverage with tests

### Verify GraphQL operations changes

Use doc/concept/spring/endpoint.adoc as the implementation baseline.
- Check schema files against entity definitions
- Check coverage with tests

## Validation checklist

- [ ] Implementation classes compile
- [ ] Tests execute without errors
