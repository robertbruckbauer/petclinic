---
name: bump-java-version
description: Update all repository Java runtime and build references to a new java version and validate the build workflow configuration.
---

## Precondition

If the local Java runtime major version is not greater than or equal to `<version>` then

- Stop immediately.
- Do not edit files.
- Response with `Your local Gradle JVM is too old for source level <version>`.

## Inputs

- `version`: Java version with long time support, e.g. `25`

## Procedure

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
gradle build
```

Run from repository root.
This step must not fail.
