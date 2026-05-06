---
name: bump-node-version
description: Bump the Node runtime version, and validate the build workflow.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target version

Extract the version from the request.
Check if version has major number only, e.g. `24`.
Replace placeholder `{version}` with version.

### Validate target version

If the local Node runtime major version is not greater than or equal to `{version}` then
- Response with `Your local Node runtime is too old for source level {version}`.
- Stop immediately.
- Do not edit files.

## Task steps

Use this skill to bump node `{version}` based on `doc/manual/setup-node.adoc`.

### Update `app/client-angular/Dockerfile`

- Set the base image to `node:{version}-alpine@sha256:...`.

### Update `app/client-svelte/Dockerfile`

- Set the base image to `node:{version}-alpine@sha256:...`.

### Update `.github/workflows/build.yml`

- In action `setup-node` set `node-version` to `"{version}"`.

### Update `.devcontainer/devcontainer.json`

- In element `ghcr.io/devcontainers/features/node:1` set `"version": "{version}"`.

### Validate changes

```bash
pushd app/client-angular
pnpm install && pnpm run prettierCheck && pnpm run build && pnpm run test
popd
pushd app/client-svelte
pnpm install && pnpm run prettierCheck && pnpm run build && pnpm run test
popd
pushd app/tester
pnpm install && pnpm run prettierCheck && pnpm run build && pnpm run test
popd
```

Run from repository root.
This step must not fail.
