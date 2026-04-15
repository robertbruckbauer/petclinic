---
name: bump-node-version
description: Bump the Node runtime version, and validate the build workflow.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target version

Extract the version from the request.
Check if version has major number only, e.g. `24`.
Replace placeholder `<version>` with version.

### Validate target version

If the local Node runtime major version is not greater than or equal to `<version>` then
- Response with `Your local Node runtime is too old for source level <version>`.
- Stop immediately.
- Do not edit files.

## Task steps

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
## Task output

Create a short summary with files changed.
