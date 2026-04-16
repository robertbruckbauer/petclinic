---
name: bump-gradle-version
description: Bump the Gradle runtime version, validate all plugin dependencies, and validate the build workflow.
---

## Task preconditions

You MUST NOT generate code if even one of the preconditions is not met.

### Identify target version

Extract the version from the request.
Check if version has major and minor number, e.g. `8.14`.
Patch level is optional but can be supplied, e.g. `9.4.1`.
Replace placeholder `<version>` with version.

### Validate target version

If the local Gradle runtime version is not greater than or equal to `<version>` then
- Response with `Your local Gradle runtime is too old for source level <version>`.
- Stop immediately.
- Do not edit files.

If any deprecation would break the build with the target version then
- Response with `Gradle version <version> has breaking changes in [script names].`
- Stop immediately.
- Do not edit files.

### Validate plugins

Verify that all plugins listed in `settings.gradle` have versions compatible with the target Gradle version, e.g.
- Check [Spring Boot Gradle Plugin](https://plugins.gradle.org/plugin/org.springframework.boot)
- Check [Spring Dependency Management Gradle Plugin](https://plugins.gradle.org/plugin/io.spring.dependency-management)
- Check [Spotless Gradle Plugin](https://plugins.gradle.org/plugin/com.diffplug.spotless)
- Check [Jib Gradle Plugin](https://plugins.gradle.org/plugin/com.google.cloud.tools.jib)
- Check [Git-Version Gradle Plugin](https://plugins.gradle.org/plugin/com.palantir.git-version)
- Check [Node Gradle Plugin](https://plugins.gradle.org/plugin/com.github.node-gradle.node)
- Check [AsciidoctorJ Gradle Plugin](https://plugins.gradle.org/plugin/org.asciidoctor.jvm.convert)

If any plugin has a breaking change or deprecation that would prevent the build from completing then
- Response with `Gradle version <version> has breaking changes in [plugin names].`
- Stop immediately.
- Do not edit files.
eholder `<package>` with the given name.

## Task steps

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
