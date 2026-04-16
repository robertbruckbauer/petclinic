---
name: Domain entities sanity checker
description: |
  Performs a comprehensive sanity check of domain entities.
  Validates structure, implementation and tests.
  Checks compliance of implementation files with the implementation guides.
---

## Task steps

### Compare entity fact sheets with entity implementation


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
