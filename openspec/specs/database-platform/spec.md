# Database Platform

Database and migration technology for the backend — which engine is used in which environment, how Liquibase migrations execute, and how `app/migrate` performs that migration. Per `openspec/AGENTS.md`, any capability suffixed `-platform` is technology-specific by design: the requirements here are about the technology itself, not an abstraction over it. `app/migrate`'s module boundaries, dependencies, and packaging stay in `backend-platform`; this capability covers only the database engine and migration behavior itself. Liquibase changesets live in `lib/backend-data/src/main/resources/liquibase`.

## Requirements

### Requirement: HyperSQL is mandatory for development and test, PostgreSQL is mandatory for production-like use

HyperSQL (in-memory) and PostgreSQL are the only two supported database backends, and each has exactly one role: HyperSQL for development and the executed test suite, PostgreSQL for a production-like deployment. This is not incidental — no other database engine is introduced, and neither backend is used outside its assigned role (e.g. running the test suite against PostgreSQL, or deploying production against HyperSQL). Both backends run the same Liquibase changelog (`lib/backend-data`'s `liquibase/changelog.xml` and its included `v1/*.xml` files) — there is no per-database changelog branch.

#### Scenario: The same changelog targets either database
- **GIVEN** the packaged Liquibase changelog in `lib/backend-data`
- **WHEN** it is applied against a HyperSQL datasource or against a PostgreSQL datasource
- **THEN** the resulting schema serves the same JPA entity mappings in `lib/backend-api` either way

#### Scenario: Development and the test suite default to HyperSQL, with no external database required
- **GIVEN** `app/server`'s default configuration (`spring.datasource.url=jdbc:hsqldb:mem:db`) and `app/deploy/compose.yml`'s dev topology
- **WHEN** the application is run locally or the test suite is executed
- **THEN** it runs against an in-memory HyperSQL instance the JVM itself owns — no separate database server needs to be started or reachable first

#### Scenario: A production-like deployment runs against PostgreSQL
- **GIVEN** `app/deploy/compose-pg.yml`'s topology (`SPRING_DATASOURCE_URL=jdbc:postgresql://postgres18:5432/...`)
- **WHEN** the production-like stack is started
- **THEN** `app/server` and `app/migrate` both connect to the PostgreSQL service, not to an in-memory HyperSQL instance

#### Scenario: No third database engine is introduced
- **GIVEN** the build files of `lib/backend-data` and `app/migrate`, which declare the JDBC driver dependencies
- **WHEN** a change adds a new database driver dependency
- **THEN** HyperSQL and PostgreSQL remain the only supported database engines — adding a third (e.g. MySQL, Oracle, H2) is a violation of this requirement, not a matter of preference

### Requirement: Migrations run embedded on HyperSQL, standalone on PostgreSQL

Against HyperSQL, `app/server` applies the changelog itself at startup via Spring Boot's Liquibase auto-configuration — no separate step is required. Against PostgreSQL, `app/server`'s own Liquibase auto-run is disabled (`SPRING_LIQUIBASE_ENABLED=false`); a separate `app/migrate` process must run to completion first.

#### Scenario: Dev startup applies the changelog in-process
- **GIVEN** `app/server` configured with a HyperSQL datasource and Liquibase auto-configuration enabled (the default)
- **WHEN** the application starts
- **THEN** the changelog is applied before the application reports itself healthy, with no external migration step involved

#### Scenario: Production-like startup requires migrate to finish first
- **GIVEN** `app/server` configured with a PostgreSQL datasource and `SPRING_LIQUIBASE_ENABLED=false`
- **WHEN** the deployment topology starts `app/server` and `app/migrate` together
- **THEN** `app/server` does not start serving requests until `app/migrate` has exited successfully against the same database/schema

### Requirement: app/migrate is a standalone runner, not a Spring Boot application

`app/migrate`'s entry point creates no Spring application context. It reads four required environment variables (`DATABASE_SCHEMA`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_LIQUIBASE_CHANGE_LOG`), creates the target schema if it does not already exist, then applies the changelog directly through the Liquibase Java API.

#### Scenario: A missing required variable stops the run before any database connection is attempted
- **GIVEN** `app/migrate` is started without one of its required environment variables set
- **WHEN** it runs
- **THEN** it reports which variable is missing and does not attempt to connect to any database
