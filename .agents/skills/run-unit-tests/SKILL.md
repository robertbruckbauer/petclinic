---
name: run-unit-tests
description: 'Run all unit tests via the gradle check task, covering Java tests and both Angular/Svelte client test suites; use for prompts like "Run all unit tests" or "Run the test suite".'
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Implementation request

The request must be an implementation request.
An implementation request is any request that changed files under `app/`, `lib/`, or `buildSrc`.

## Task steps

### Run all unit tests

Run the `check` task to execute all unit tests across backend and frontend modules.

```bash
gradle check
```

Run from the repository root to test all modules at once, or scope to a single module.

This step must not fail before reporting an implementation request as done.

## Validation checklist

- [ ] All Java unit tests pass
- [ ] Angular vitest suite passes
- [ ] Svelte vitest suite passes
- [ ] No test failure was suppressed or ignored

## Task output

Report the overall result (pass/fail) and list any failing test names.
