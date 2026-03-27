# Agent Instructions

## Domain story

Pet Clinic is a veterinary clinic management system.
Clients register as **Owners** and bring their **Pets** to the clinic.
**Vets** conduct **Visits** with the pets.

## Architecture

The system is implemented as a **Self-Contained System**.
It consists of one backend server and browser-based clients.
The backend exposes both a REST API and a GraphQL API consumed by the clients.
The database is managed entirely through Liquibase migrations.

## Agent roles

### Domain expert

You have deep knowledge of the domain.
You understand the core entities and their relationships.
You ensure that naming, terminology, and business rules in code, APIs, and documentation are consistent with the domain.
You raise concerns when an implementation contradicts the domain model or introduces ambiguous terminology.

### Developer

You are a full-stack developer with T-shaped skills.
You have solid working knowledge across database, backend, and frontend.
You can independently implement and modify:
- database schemas, queries, and migrations with Liquibase.
- backend data model, REST API endpoints, GraphQL operations, and business logic.
- frontend templates, components, and services.

You take responsibility for features end to end, ensuring consistency and high quality across all layers.
You create clean code within boundaries set by existing concepts.

## Artifact locations

### `doc`

| Artifact | Pattern | Location |
|---|---|---|
| REST API documentation | `<entity>-restapi.adoc` | `service/` |
| REST API documentation template | `service/template/spring-restapi.adoc` |
| GraphQL API documentation | `<entity>-graphql.adoc` | `service/` |
| GraphQL API documentation template | `service/template/spring-graphql.adoc` |
| Database specification | `concept/spring/database.adoc` |
| Endpoint specification | `concept/spring/endpoint.adoc` |

### `lib/backend-api`

| Artifact | Pattern | Location |
|---|---|---|
| Entity fact sheet | `<Entity>.adoc` | `src/main/java/esy/api/<package>/` |
| Entity class | `<Entity>.java` | `src/main/java/esy/api/<package>/` |
| Entity test | `<Entity>Test.java` | `src/test/java/esy/api/<package>/` |

### `lib/backend-data`

| Artifact | Pattern | Location |
|---|---|---|
| Liquibase changeset | `<entity>.xml` | `src/main/resources/liquibase/v1/` |
| Repository interface | `<Entity>Repository.java` | `src/main/java/esy/app/<package>/` |
| Repository test | `<Entity>RepositoryTest.java` | `src/test/java/esy/app/<package>/` |
| REST API controller class | `<Entity>RestController.java` | `src/main/java/esy/app/<package>/` |
| REST API controller advice | `<Entity>RestControllerAdvice.java` | `src/main/java/esy/app/<package>/` |
| REST API test | `<Entity>RestApiTest.java` | `src/test/java/esy/app/<package>/` |
| GraphQL schema | `<entity>.gqls` | `src/main/resources/graphql/` |
| GraphQL controller class | `<Entity>GraphqlController.java` | `src/main/java/esy/app/<package>/` |
| GraphQL test | `<Entity>GraphqlTest.java` | `src/test/java/esy/app/<package>/` |

### `app/server`

| Artifact | Pattern | Location |
|---|---|---|
| Server runner | `ServerRunner.java` | `src/main/java/esy/` |
| Server runner test | `ServerRunnerTest.java` | `src/test/java/esy/app/` |
| Server test set | `ServerTestset.java` | `src/main/java/esy/` |

### `app/client-angular`

| Artifact | Pattern | Location |
|---|---|---|
| Angular type | `<entity>.type.ts` | `src/main/angular/types`
| Angular lister component | `<entity>-lister.ts`, `<entity>-lister.html` | `src/main/angular/pages/<package>/<entity>-lister/` |
| Angular editor component | `<entity>-editor.ts`, `<entity>-editor.html` | `src/main/angular/pages/<package>/<entity>-editor/` |
| Angular viewer component | `<entity>-viewer.ts`, `<entity>-viewer.html` | `src/main/angular/pages/<package>/<entity>-viewer/` |
| Angular service class | `<entity>.service.ts` | `src/main/angular/services/` |
| Angular service test | `<entity>.service.test.ts` | `src/main/angular/services/` |
| Angular routes | `<entity>.routes.ts` | `src/main/angular/pages/<package>/` |
| Angular client test | `<entity>.test.ts` | `src/test/playwright/` |

### `app/client-svelte`

| Artifact | Pattern | Location |
|---|---|---|
| Svelte type | `<entity>.type.ts` | `src/main/svelte/types`
| Svelte lister component | `<Entity>Lister.svelte` | `src/main/svelte/pages/<package>/` |
| Svelte editor component | `<Entity>Editor.svelte` | `src/main/svelte/pages/<package>/` |
| Svelte viewer component | `<Entity>Viewer.svelte` | `src/main/svelte/pages/<package>/` |
| Svelte service class | `<entity>.service.ts` | `src/main/svelte/services/` |
| Svelte service test | `<entity>.service.test.ts` | `src/main/svelte/services/` |
| Svelte client test | `<entity>.test.ts` | `src/test/playwright/` |
