---
name: create-new-version
description: Bump repository version to a new major.minor release, validate consistency, commit with a dedicated message, and create a release tag.
---

## Inputs

- `majorMinor`: version in `major.minor` format, e.g. `1.4`
- `patchVersion = <majorMinor>.0`, e.g. `1.4.0`

## Procedure

Use this skill to apply a new version for this repository based on the information in `doc/manual/setup-version.adoc`.

### Update VERSION file.

Set `VERSION` to `<majorMinor>`.

### Update app/client-angular/package.json

```bash
pushd app/client-angular
npm pkg set version=<patchVersion>
npm install --package-lock-only
popd
```

### Update app/client-svelte/package.json

```
pushd app/client-svelte
npm pkg set version=<patchVersion>
npm install --package-lock-only
popd
```

### Update app/deploy/Chart.yaml

Update version fields in `app/deploy/Chart.yaml` so major and minor match `<majorMinor>`.

### Validate version consistency

```bash
gradle versionCheck
```

Run task in the repository root.
This step must not fail.

### Commit and push version changes

```bash
git add .
git commit -m 'version: <majorMinor>'
git push
```

### Create and push version tag

```bash
git tag <majorMinor>
git push --tags
```

The GitHub actions workflow listens for tags and executes release steps for that tagged commit.
