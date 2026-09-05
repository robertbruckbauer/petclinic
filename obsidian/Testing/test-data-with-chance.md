---
category: Testing
related:
  - vitest-conventions.md
  - ../Database/test-data-initialization.md
status: current
updated: 2026-09-05
---

# Test data with `chance`

Both clients depend on the `chance` library (a devDependency in each client's `package.json`) for generating realistic-looking random fixture data in frontend tests — names, addresses, etc. — instead of hand-typed placeholder strings repeated across every test file. This keeps fixture data varied enough to catch assumptions like "field is always short" without every test author inventing their own fake data generator.
