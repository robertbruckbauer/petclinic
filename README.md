---
cwd: .
---

# README

Self-contained pet clinic system with a Spring Boot backend, Spring Data JPA (including QueryDSL), Spring Data REST, Spring GraphQL, and Liquibase schema migration (HyperSQL or PostgreSQL).

## Versioning

Show the current application version (semantic versioning via lightweight tags).

```sh
./gradlew versionTag

```

Show changes in the current release (conventional commits; may be empty).

```sh
./gradlew versionRelease

```

Show all past release changes, grouped by version, newest first.

```sh
./gradlew versionHistory

```

Check that major/minor versions match across `VERSION` and other files (e.g. `Chart.yaml`).
`VERSION` must not contain a line break.

```sh
./gradlew versionCheck

```

## Building and testing

```sh
./gradlew clean build

```

```sh
./gradlew buildImage

```

```sh
./gradlew pages

```

## Formatting

Check formatting rules without changing source (`spotlessCheck` for Java).

```sh
./gradlew lint

```

Apply formatting rules to source (`spotlessApply` for Java).

```sh
./gradlew format

```

## Starting backend locally

Start the backend as a Java application on port 8080.
HAL Explorer: `localhost:8080/api/explorer`.
GraphQL Explorer: `localhost:8080/api/graphiql`.

```sh
./gradlew app:backend:bootRun

```

Optional Hibernate logging via environment variables:
`LOGGING_LEVEL_ORG_HIBERNATE_TYPE=TRACE` for bind parameters;
`LOGGING_LEVEL_ORG_HIBERNATE_STAT=DEBUG` for statement metrics.

## Starting application locally

Run the server with the embedded HyperSQL database.
Backend: `localhost:8080`.

```sh
./gradlew deployUp

```

Run PostgreSQL, Liquibase migration, and the server together.
Backend: `localhost:8080`.

```sh
./gradlew deployUp -PcomposeFile=compose-pg.yml

```

Stop all containers.

```sh
./gradlew deployDown

```
