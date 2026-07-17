---
name: bump-release-version
description: Bump the release version of this repository, validate consistency, commit changes with a dedicated message, and create a release tag to trigger the actions workflow.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target version

Extract the version from the request.
Check if version has major number only, e.g. `1.4`.
Replace placeholder `{version}` with version.

### Validate target version

If the local release version is greater than or equal to `{version}` then
- Response with `Your local release version is greater than or equal to {version}`.
- Stop immediately.
- Do not edit files.

## Task steps

Use this skill to apply a new version for this repository based on the information in `doc/manual/setup-version.adoc`.

### Update VERSION file.

Set content of `VERSION` file to `{version}`.
Do not add a line break.

### Update app/client-angular/package.json

```bash
pushd app/client-angular
npm pkg set version={version}.0
npm install --package-lock-only
popd
```

### Update app/client-svelte/package.json

```
pushd app/client-svelte
npm pkg set version={version}.0
npm install --package-lock-only
popd
```

### Update app/deploy/Chart.yaml

Update version fields in `app/deploy/Chart.yaml` so major and minor match `{version}`.

### Validate version consistency

```bash
gradle versionCheck
```

Run task in the repository root.
This step must not fail.

### Commit and push version changes

```bash
git add .
git commit -m 'version: {version}'
git push
```

### Create and push version tag

```bash
git tag {version}
git push --tags
```

The GitHub actions workflow listens for tags and executes release steps for that tagged commit.
