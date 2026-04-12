---
name: bump-gradle-version
description: Update the Gradle runtime version and all plugin dependencies, then validate the build workflow configuration.
---

## Precondition

If the local Gradle runtime version is not greater than or equal to `<version>` then

- Stop immediately.
- Do not edit files.
- Response with `Your local Gradle runtime is too old for source level <version>`.

If any deprecation would break the build with the target version then

- Stop immediately.
- Do not edit files.
- Response with `Gradle version <version> has breaking changes in [script names].`

Verify that all plugins listed in `settings.gradle` have versions compatible with the target Gradle version, e.g.

- Check [Spring Boot Gradle Plugin](https://plugins.gradle.org/plugin/org.springframework.boot)
- Check [Spring Dependency Management Gradle Plugin](https://plugins.gradle.org/plugin/io.spring.dependency-management)
- Check [Spotless Gradle Plugin](https://plugins.gradle.org/plugin/com.diffplug.spotless)
- Check [Jib Gradle Plugin](https://plugins.gradle.org/plugin/com.google.cloud.tools.jib)
- Check [Git-Version Gradle Plugin](https://plugins.gradle.org/plugin/com.palantir.git-version)
- Check [Node Gradle Plugin](https://plugins.gradle.org/plugin/com.github.node-gradle.node)
- Check [AsciidoctorJ Gradle Plugin](https://plugins.gradle.org/plugin/org.asciidoctor.jvm.convert)

If any plugin has a breaking change or deprecation that would prevent the build from completing then

- Stop immediately.
- Do not edit files.
- Response with `Gradle version <version> has breaking changes in [plugin names].`

## Inputs

- `version`: Gradle version, e.g. `8.14`

## Procedure

Use this skill to bump Gradle `<version>` based on `doc/manual/setup-gradle.adoc`.

### Update `gradle/wrapper`

```bash
gradle wrapper --gradle-version <version>
```

### Update `.github/workflows/build.yml`

- `node-version: "<version>"`

### Update `.devcontainer/devcontainer.json`

- `"gradleVersion": "<version>"`

### Validate changes

```bash
gradle build --parallel
```

Run from repository root.
This step must not fail.
