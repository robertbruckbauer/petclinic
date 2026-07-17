---
name: lint-and-format
description: 'Lint and format source code using the gradle tasks. Run lint to verify source files are correctly formatted. Run format to apply changes on the source code to automatically fix formatting issues. Java code is processed by Spotless; TypeScript code is processed by Prettier.'
---

## Task steps

### Check formatting (lint)

Run the `lint` task to verify all source files are correctly formatted.

```bash
gradle lint
```

Run from the repository root to check all modules at once, or scope to a single module.

This step must not fail before committing code.

### Apply formatting (format)

Run the `format` task to automatically fix formatting issues.

```bash
gradle format
```

Run from the repository root to format all modules at once, or scope to a single module.

After running `format`, run `lint` to confirm no remaining issues:

```bash
gradle lint
```

This step must not fail.
