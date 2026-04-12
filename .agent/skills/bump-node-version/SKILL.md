---
name: bump-node-version
description: Update all repository node runtime and build references to a new node version and validate the build workflow configuration.
---

## Precondition

If the local node major version is not greater than or equal to `<version>` then

- Stop immediately.
- Do not edit files.
- Response with `Your local Node runtime is too old for version <version>`.

## Inputs

- `version`: Node version with long term support, e.g. `24`

## Procedure

Use this skill to bump node `<version>` based on `doc/manual/setup-node.adoc`.

### Update `app/client-angular/Dockerfile`

- Set the base image to `node:<version>-alpine@sha256:...`.

### Update `app/client-svelte/Dockerfile`

- Set the base image to `node:<version>-alpine@sha256:...`.

### Update `.github/workflows/build.yml`

- In tag `setup-node`, set `node-version` to `"<version>"`.

### Update `.devcontainer/devcontainer.json`

- In tag `setup-node`, set `"version"` to `"<version>"`.

### Validate changes

```bash
sh -xv app/client-angular/build.npm.sh ci
sh -xv app/client-svelte/build.npm.sh ci
```

Run from repository root.
This step must not fail.
