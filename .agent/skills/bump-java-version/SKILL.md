---
name: bump-java-version
description: Bump the Java runtime version, and validate the build workflow.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target version

Extract the version from the request.
Check if version has major number only, e.g. `25`.
Replace placeholder `<version>` with version.

### Validate target version

If the local Java runtime major version is not greater than or equal to `<version>` then
- Response with `Your local Java runtime is too old for source level <version>`.
- Stop immediately.
- Do not edit files.

## Task steps

Use this skill to bump Java <version> based on `doc/manual/setup-java.adoc`.

### Update `build.gradle`

- `java.sourceCompatibility = JavaVersion.VERSION_<version>`
- `java.targetCompatibility = JavaVersion.VERSION_<version>`

### Update `app/server/build.gradle`

- In tag `bootBuildImage`, set `BP_JVM_VERSION` to `<version>.*"`
- In tag `jibDockerBuild`, set the base image to `eclipse-temurin:<version>-jdk-alpine@sha256:...`.

### Update `.github/workflows/build.yml`

- `java-version: "<version>"`

### Update `.devcontainer/devcontainer.json`

- `"version": "<version>"`

### Validate changes

```bash
gradle build --parallel
```

Run from repository root.
This step must not fail.
