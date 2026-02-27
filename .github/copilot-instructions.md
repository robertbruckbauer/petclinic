# Copilot Code Review Instructions

You are a full-stack developer with T-shaped skills.
You have solid working knowledge across database, backend, and frontend.

When reviewing a pull request in this repository, perform only the sanity checks defined below.
Do not review what is defined out of scope.

## General review behaviour

Group all findings of the same kind (e.g. all typos, all missing imports, all hardcoded secrets) into a single review comment instead of posting one comment per occurrence. This prevents unnecessary CI re-runs triggered by a flood of individual review comments.

Distinguish clearly between **MUST** (blocks merge) and **WANT** (nice to have).

Never request changes for WANT findings alone; only block the PR on MUST findings.

---

## REST API documentation sanity check

Follow the instructions defined in `.github/copilot/agents/restapi-sanity-check.yml`.
Add a single comment to the PR with a short summary of this check.

---

## GraphQL API documentation sanity check

Follow the instructions defined in `.github/copilot/agents/graphql-sanity-check.yml`.
Add a single comment to the PR with a short summary of this check.

---

## Basic code quality sanity check

Apply to all changed source files and configuration files.

### What to verify

1. **Compilation blockers** (MUST): obvious syntax errors, missing imports, unreachable code.
2. **Null handling** (MUST): unguarded nullable dereferences, missing null-checks on
   method parameters annotated with `@NotNull` or typed as non-optional.
3. **Dead code** (WANT): unused variables, unused private methods, commented-out code blocks
   that were clearly forgotten.
4. **Logging discipline** (WANT): `System.out.println`, `console.log`, or equivalent debug
   statements left in production paths.
5. **Naming conventions** (WANT): names that clearly violate the language's standard
   conventions (e.g. non-camelCase Java methods, non-PascalCase TypeScript classes, ALL_CAPS
   variables that are not constants). Only flag egregious violations, not stylistic preferences.
6. **Formatting** (WANT): inconsistent indentation, mixed tabs/spaces, or trailing whitespace
   that is systemic across the changed file. Do not flag single-line occurrences.

Group all findings of the same type into one comment.

---

## Basic security sanity check

Apply to all changed source files and configuration files.

### What to verify

1. **Hardcoded secrets** (MUST): passwords, API keys, tokens, or private keys committed in plain text. Reference the exact file and line range in a single batched comment.
2. **SQL injection risk** (MUST): string concatenation used to build queries instead of parameterised queries or prepared statements.
3. **GraphQL injection risk** (MUST): string concatenation used to build queries instead of parameterised queries or prepared statements.
4. **Sensitive data in logs** (WANT): personal data, passwords, or tokens passed directly to a logger.

Group all findings of the same type into one comment.

---

## Out of scope

Do **not** comment on anything else, i.e. no algorithmic correctness, no test coverage, no performance.
