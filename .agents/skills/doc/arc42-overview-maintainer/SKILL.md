---
name: arc42-overview-maintainer
description: 'Update an arc42 chapter, keeping it at bird''s-eye level; use for prompts like "Update arc42 chapter ... for ..." or "Reflect this architectural change in arc42" or "Add a architectural decision about ..." or "Add a architectural risk about ..." or "Add a architectural debt about ...".
---

## Task preconditions

You MUST NOT generate a chapter edit if even one of the preconditions is not met.

### Identify target chapter

Extract the chapter number/title from the request.
Check that `arc42/{NN}-{title}/` exists among the 12 standard chapter folders.
Replace placeholder `{NN}-{title}` with the matched folder name.
Edit the appropriate file(s) inside that folder (see chapter-specific format rules below).
If `arc42/index.adoc` must list a newly added file, add its `include::` there as well.

## Task steps

### Apply the removal test before adding anything

For each candidate sentence: would removing it change a stakeholder's understanding of the architecture, or an architect's fundamental implementation choice?
If not, it belongs in `obsidian/` instead — write it there (via `obsidian-maintainer`) and do not inline it in the chapter.

### Edit the chapter

Keep the chapter's existing content boundary — each of the 12 standard arc42 chapters is scoped to one facet (introduction/goals, constraints, context and scope, solution strategy, building blocks, runtime view, deployment view, cross-cutting concepts, architecture decisions, quality requirements, risks and debt, glossary) and stays within it.
Add or update only what's architecturally significant.
Do not add implementation details.
Do not link to `openspec`.
Do not link to `obsidian`.

Keep `:relrootdir: ../..` on chapter files under `arc42/{NN}-…/` and use `xref:{relrootdir}/arc42/...` (and `{relrootdir}/doc/concept/...` where ch. 8 allows).
Do not switch to file-relative-only xrefs.

### Apply the chapter-specific format rule

Each of the 12 chapters has its own established format below (headings, table shape, list style, file layout).
Match it — do not introduce a new structure for a chapter that already has one.

#### 1. Introduction and Goals (`arc42/01-introduction-and-goals/`)

Describes the relevant requirements and the driving forces that software architects and development team must consider.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-1/) for more information.

- `introduction.adoc` — level 2 section `Domain story` shows a few sentences naming the core entities, linking to chapter 12 glossary definitions rather than restating them.
- `goals.adoc` — level 2 section `Goals` shows product goals, each with a level 3 title an a management summary.
- `stakeholder.adoc` — level 2 section `Stakeholder` shows a `Role | Concern` table, one row per stakeholder without process detail.

Nest under level 1 section `1. Introduction and Goals` in `arc42/index.adoc`.

#### 2. Constraints (`arc42/02-constraints/constraints.adoc`)

Any requirement that constrains software architects in their freedom of design and implementation decisions or decision about the development process. These constraints sometimes go beyond individual systems and are valid for whole organizations and companies.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-2/) for more information.

- `constraints.adoc` - level 2 section `Constraints` shows constraints, each with a level 3 title an a management summary.;

Nest under level 1 section `2. Constraints` in `arc42/index.adoc`.

#### 3. Context and Scope (`arc42/03-context-and-scope/`)

System scope and context - as the name suggests - delimits your system (i.e. your scope) from all its communication partners (neighboring systems and users, i.e. the context of your system). It thereby specifies the external interfaces.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-3/) for more information.

- `context.adoc` — `Business context` shows actors and external systems in a PlantUML usecase diagram; `Technical context` shows system context in a PlantUML C4 diagram (level 1).
- `scope.adoc` — `== Scope` heading; in/out of scope.

Nest under level 1 section `3. Context and Scope` in `arc42/index.adoc`.

#### 4. Solution Strategy (`arc42/04-solution-strategy/`)

A solution strategy is a system-wide, hard-to-reverse decision.
A decision that isn't strategic doesn't get a place here — apply the removal test.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-4/) for more information.

Each strategy is its own slug-named `.adoc` with an level 2 section and a very short bird's-eye description without implementation details.
Add a new file for a new concept and `include::` it there under that title.

Nest under level 1 section `4. Solution Strategy` in `arc42/index.adoc`.

#### 5. Building Block View (`arc42/05-building-block-view/overview.adoc`)

The building block view shows the static decomposition of the system into building blocks (modules, components, subsystems, classes, interfaces, packages, libraries, frameworks, layers, partitions, tiers, functions, macros, operations, data structures, …) as well as their dependencies (relationships, associations, …).
Visit the [Arc42 online documentation](https://docs.arc42.org/section-5/) for more information.

`overview.adoc` uses nested module headings under level 2 sections `Applications` and `Libraries` for top-level modules, each with a short responsibility and optional technology links.

Nest under level 1 section `5. Building Block View` in `arc42/index.adoc`.

#### 6. Runtime View (`arc42/06-runtime-view/overview.adoc`)

The runtime view describes concrete behavior and interactions of the system’s building blocks in form of scenarios.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-6/) for more information.

`overview.adoc` holds a stub — no runtime scenarios are documented yet.

Nest under level 1 section `6. Runtime View` lives in `arc42/index.adoc`.

#### 7. Deployment View (`arc42/07-deployment-view/overview.adoc`)

The deployment view describes technical infrastructure and the mapping of (software) building blocks to that infrastructure elements.
Often systems are executed in different environments, e.g. local development environment, test environment, production environment. In such cases you should document all relevant environments.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-7/) for more information.

Nest under level 1 section `7. Deployment View` lives in `arc42/index.adoc`.

#### 8. Cross-cutting Concepts (`arc42/08-cross-cutting-concepts/`)

This section describes crosscutting concepts (practices, patterns, regulations or solution ideas).
Visit the [Arc42 online documentation](https://docs.arc42.org/section-8/) for more information.

Each concept is its own slug-named `.adoc` with an level 2 section and a very short bird's-eye description without implementation details.
Add a new file for a new concept and `include::` it there under that title.

Nest under level 1 section `8. Cross-cutting Concepts` in `arc42/index.adoc`.

#### 9. Architecture Decisions (`arc42/09-architecture-decisions/`)

Important, expensive, large scale or risky architecture decisions including rationales.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-9/) for more information.

Each decision is its own slug-named `NNNN-*.adoc` with a standard structure.

Create or update risks via `arc42-adr-maintainer`.

Index only in `overview.adoc` in a level 2 section `Decision index`; one `ADR | Decision | Status` table row per decision.
Add a new file for a new decision and `xref::` it in the table.

Nest under level 1 section `9. Architecture Decisions` in `arc42/index.adoc`.

#### 10. Quality Requirements (`arc42/10-quality-requirements/overview.adoc`)

This section contains all relevant quality requirements.
A quality goal is a system-wide, hard-to-reverse goal.
A goal that isn't strategic doesn't get a place here — apply the removal test.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-10/) for more information.

Each quality goal is level 2 section with a very short bird's-eye description without implementation details.

Nest under level 1 section `10. Quality Requirements` lives in `arc42/index.adoc`.

#### 11. Risks and Debt (`arc42/11-risks-and-debt/`)

A list of identified technical risks or debts, ordered by priority.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-11/) for more information.

- `risks.adoc` — level 2 section `Risks` containing risks as a table: `Risk | Score | Mitigation`, Score = likelihood × impact (each 1–3), Mitigation may be empty when none is planned.
- `debts.adoc` — level 2 section `Debts` containing any significant debt (not only technical).
  Use a clear heading and short description per debt item.

Create or update risks via `arc42-risk-maintainer`.

Nest under level 1 section `11. Risks and Debt` in `arc42/index.adoc`.

#### 12. Glossary (`arc42/12-glossary/`)

The most important domain and technical terms that your stakeholders use when discussing the system.
Visit the [Arc42 online documentation](https://docs.arc42.org/section-12/) for more information.

- `domain.adoc` — level 2 section `Domain` with included entity fact sheet (definition-list style) — never hand-written.
- `terms.adoc` — level 2 section `Terms` with architecture terms as a hand-written one- or two-sentence definition each (definition-list style).

Nest under level 1 section `12. Glossary` in `arc42/index.adoc`.

## Validation checklist

- [ ] No sentence added fails the removal test
- [ ] No implementation-level detail was added directly to the chapter
- [ ] The chapter's content boundary wasn't widened without reason
- [ ] `:includedir:` is present and includes use `{includedir}/...`
- [ ] `:relrootdir:` is present and xrefs use `{relrootdir}/...`
- [ ] No file under `plans/` is referenced from the chapter

## Task output

Report which chapter/file was edited, what was added/changed, and what (if anything) was moved out.
