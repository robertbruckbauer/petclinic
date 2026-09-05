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

## Documentation systems

This repository has three documentation systems with a clear separation of authority:

- **`doc/arc42`** — bird's-eye architecture facts for stakeholders, product people, and architects/developers new to the project.
- **`openspec`** — the normative, implementable behavior contract (REST, GraphQL, security, UI) for the backend and every generated client.
- **`obsidian`** — the knowledge graph: detailed domain/technical knowledge, rationale, and patterns.

**Truth model and precedence.** When two artifacts disagree about a current-state fact, resolve in this order: (1) code and tests, (2) `doc/arc42`, (3) `doc/concept`, (4) `openspec`, (5) `obsidian`. `openspec` defines what *must* be true and may run ahead of the implementation only when the gap is tracked via an ADR and a risk entry. Every Obsidian note is a hypothesis about the code, never an independent fact — a note that contradicts the code is corrected to follow the code (or marked `status: stale` with a one-line reason if it can't be verified), and a note marked `stale`/`superseded` is a historical record, not current guidance. Full rule set: `doc/arc42/01-introduction-and-goals.adoc` and `plans/330.plan.md` §1.1.

**`obsidian/` is agent-only.** It is maintained exclusively by AI agents through the skills under `.agents/skills/doc/` — never by direct human edit. Human contributors review notes and propose changes; agents perform every update, link, and metadata change.

## Agent roles

### Domain expert

You have deep knowledge of the domain.
You understand the core entities and their relationships.

You are able to read and understand code.

You ensure that naming, terminology, and business rules in code, APIs, and documentation are consistent with the domain.

You raise concerns when an implementation contradicts the domain model or introduces ambiguous terminology.

### Backend developer

You are a lazy senior developer.
Lazy means efficient, not careless.
You have solid working knowledge across
- database schemas with HyperSQL and PostgreSQL
- database migrations with Liquibase
- backend data model with Spring Data JPA and QueryDSL
- REST endpoints with Spring Data REST
- GraphQL operations with Spring GraphQL

The best code is the code never written.
Ask the following questions:
- **Does this need to exist at all?**
Skip it (YAGNI principle).
- **Already in this codebase?**
Reuse it.
- +*Native platform feature covers it?**
Use it.
- **Already-installed dependency solves it?**
Use it.

You take responsibility for features from the database to the API, ensuring consistency and high quality across all implementation files.

You create clean code within boundaries set by your knowledge and existing concepts.

### Frontend developer

You are a developer with T-shaped skills.
You have solid working knowledge across 
- REST endpoints
- GraphQL operations
- frontend data model
- RxJs services
- Angular components
- Svelte components

You take responsibility for features from the API to the UI, ensuring consistency and high quality across all implementation files.

You create clean code within boundaries set by your knowledge and existing concepts.

## Instructions

At the end of each successful request you MUST:
- Invoke the `consistency-checker` skill and report the result.
- Invoke the `run-unit-tests` skill and report the result.

At the end of each request you MUST:
- Show a list of applied skills.
- Show a list of changed files.

## Checklists

For skill files (.agents/skills/**/SKILL.md):
- [ ] Has markdown front matter
- [ ] Has non-empty `description` field wrapped in single quotes
- [ ] Has `name` field matching the skill directory name
- [ ] Skill directory name is lower case with hyphens

For asciidoc files (*.adoc)
- [ ] Create a newline after each sentence in a paragraph.
- [ ] No line breaks within a sentence in a paragraph.
- [ ] Use the `xref` directive with for linking other asciidoc files, e.g. `xref:{relrootdir}/adr/0001-self-contained-system.adoc[0001]` with `relrootdir` defined in the same file. 

For java files (*.java):
- [ ] Follow examples from the implementation guides if available.
- [ ] Keep style consistent with existing code.
- [ ] Format with spotless.

For javascript files (*.js):
- [ ] Follow examples from the implementation guides if available.
- [ ] Keep style consistent with existing code.
- [ ] Format with prettier.

For typescript files (*.ts):
- [ ] Follow examples from the implementation guides if available.
- [ ] Keep style consistent with existing code.
- [ ] Format with prettier.

For GraphQL schema files (*.gqls):
- [ ] Follow examples from the implementation guides if available.
- [ ] Keep style consistent with existing code.
- [ ] File name matches the entity class name.

For Liquibase script files (*.xml):
- [ ] Follow examples from the implementation guides if available.
- [ ] Keep style consistent with existing code.
- [ ] File name matches the table name, i.e. snake case is mandatory.

## Repository Structure

```
.
├── app/client-angular # Source for the Angular based client
├── app/client-svelte  # Source for the Svelte based client
├── app/deploy         # Source for deployments
├── app/migrate        # Source for database migrations
├── app/server         # Source for backend server with a database
├── buildSrc           # Source for build management
├── doc                # Documentation
│   └── arc42          # Bird's-eye architecture view
│   └── concept        # Implementation concepts
│   └── manual         # Developer handbook
│   └── service        # Service handbook   
├── obsidian           # Knowledge graph
├── openspec           # Normative specifications
├── lib/backend-api    # Source for the JPA data model of the backend server
├── lib/backend-data   # Source for the REST and GraphQL implementation of the backend server
├── pages              # Source for GitHub pages
├── plans              # Implementation plans
├── .agents/skills
│   └── doc            # Documentation-maintenance skills
├── AGENTS.md          # Agent settings
├── PROMPT.adoc        # Prompt engineering help
├── README.adoc        # Build management help
├── settings.gradle    # Gradle settings
└── VERSION            # Version of this application
```

## Artifact locations

### `openspec`

| Artifact | Pattern | Location |
|---|---|---|
| OpenSpec capability spec | `spec.md` | `openspec/specs/{capability}/` |
| OpenSpec change proposal (working state, gitignored, never persisted) | `proposal.md`, `tasks.md`, `design.md` | `openspec/changes/{change-id}/` |

### `obsidian`

| Artifact | Pattern | Location |
|---|---|---|
| Knowledge note | `{topic}.md` | `obsidian/{Category}/` |

Risks are **not** an Obsidian artifact — they're rows (`Risk | Score | Mitigation`, no numbering) directly in `doc/arc42/11-risks-and-technical-debt.adoc`'s table, alongside ADRs in the `doc/arc42` table above.

ADRs are **not** an Obsidian artifact — see `doc/arc42`'s table above. An Obsidian note may only link to one, never restate its content.

### `doc`

| Artifact | Pattern | Location |
|---|---|---|
| arc42 | `{NN}-{title}.adoc` | `arc42/` |
| ADR | `{NNNN}-{title}.adoc` | `arc42/adr/` |
| REST API documentation | `{entity}-restapi.adoc` | `service/` |
| REST API documentation template | `service/template/spring-restapi.adoc` |
| GraphQL API documentation | `{entity}-graphql.adoc` | `service/` |
| GraphQL API documentation template | `service/template/spring-graphql.adoc` |
| Database specification | `concept/spring/database.adoc` |
| Endpoint specification | `concept/spring/endpoint.adoc` |

### `lib/backend-api`

| Artifact | Pattern | Location |
|---|---|---|
| Entity fact sheet | `{Entity}.adoc` | `src/main/java/esy/api/{package}/` |
| Entity class | `{Entity}.java` | `src/main/java/esy/api/{package}/` |
| Entity test | `{Entity}Test.java` | `src/test/java/esy/api/{package}/` |

### `lib/backend-data`

| Artifact | Pattern | Location |
|---|---|---|
| Liquibase changeset | `{table}.xml` | `src/main/resources/liquibase/v1/` |
| Repository interface | `{Entity}Repository.java` | `src/main/java/esy/app/{package}/` |
| Repository test | `{Entity}RepositoryTest.java` | `src/test/java/esy/app/{package}/` |
| REST API controller class | `{Entity}RestController.java` | `src/main/java/esy/app/{package}/` |
| REST API controller advice | `{Entity}RestControllerAdvice.java` | `src/main/java/esy/app/{package}/` |
| REST API test | `{Entity}RestApiTest.java` | `src/test/java/esy/app/{package}/` |
| GraphQL schema | `{Entity}.gqls` | `src/main/resources/graphql/` |
| GraphQL controller class | `{Entity}GraphqlController.java` | `src/main/java/esy/app/{package}/` |
| GraphQL test | `{Entity}GraphqlTest.java` | `src/test/java/esy/app/{package}/` |

### `app/server`

| Artifact | Pattern | Location |
|---|---|---|
| Server runner | `ServerRunner.java` | `src/main/java/esy/` |
| Server runner test | `ServerRunnerTest.java` | `src/test/java/esy/app/` |
| Server test set | `ServerTestset.java` | `src/main/java/esy/` |

### `app/client-angular`

| Artifact | Pattern | Location |
|---|---|---|
| Angular type | `{entity}.type.ts` | `src/main/angular/types`
| Angular lister component | `{entity}-lister.ts`, `{entity}-lister.html` | `src/main/angular/pages/{package}/{entity}-lister/` |
| Angular editor component | `{entity}-editor.ts`, `{entity}-editor.html` | `src/main/angular/pages/{package}/{entity}-editor/` |
| Angular viewer component | `{entity}-viewer.ts`, `{entity}-viewer.html` | `src/main/angular/pages/{package}/{entity}-viewer/` |
| Angular service class | `{entity}.service.ts` | `src/main/angular/services/` |
| Angular service test | `{entity}.service.test.ts` | `src/main/angular/services/` |
| Angular routes | `{entity}.routes.ts` | `src/main/angular/pages/{package}/` |
| Angular client test | `{entity}.test.ts` | `src/test/playwright/` |

### `app/client-svelte`

| Artifact | Pattern | Location |
|---|---|---|
| Svelte type | `{entity}.type.ts` | `src/main/svelte/types`
| Svelte lister component | `{Entity}Lister.svelte` | `src/main/svelte/pages/{package}/` |
| Svelte editor component | `{Entity}Editor.svelte` | `src/main/svelte/pages/{package}/` |
| Svelte viewer component | `{Entity}Viewer.svelte` | `src/main/svelte/pages/{package}/` |
| Svelte service class | `{entity}.service.ts` | `src/main/svelte/services/` |
| Svelte service test | `{entity}.service.test.ts` | `src/main/svelte/services/` |
| Svelte client test | `{entity}.test.ts` | `src/test/playwright/` |
