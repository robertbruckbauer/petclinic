---
name: arc42-overview-maintainer
description: 'Update a doc/arc42 chapter, keeping it at bird''s-eye level and moving overflow to Obsidian; use for prompts like "Update arc42 chapter ... for ..." or "Reflect this architectural change in arc42".'
---

## Task preconditions

You MUST NOT generate a chapter edit if even one of the preconditions is not met.

### Identify target chapter

Extract the chapter number/title from the request.
Check that `doc/arc42/{NN}-{title}.adoc` exists among the 12 standard chapters.
Replace placeholder `{NN}-{title}` with the matched file name.

## Task steps

### Apply the removal test before adding anything

For each candidate sentence: would removing it change a stakeholder's understanding of the architecture, or an architect's fundamental implementation choice? If not, it belongs in `obsidian/` instead — write it there (via `obsidian-maintainer`) and link to it from the chapter, don't inline it.

### Edit the chapter

Keep the chapter's existing content boundary — each of the 12 standard arc42 chapters is scoped to one facet (introduction/goals, constraints, context and scope, solution strategy, building blocks, runtime view, deployment view, cross-cutting concepts, architecture decisions, quality requirements, risks and technical debt, glossary) and stays within it. Add or update only what's architecturally significant. Do not add implementation details, refer implementation guides in `doc/concept` only. Do not link to `openspec`. Do not link to `obsidian`.

### Apply the chapter-specific format rule

Each of the 12 chapters has its own established format below (headings, table shape, list style). Match it — do not introduce a new structure for a chapter that already has one.

#### 1. Introduction and Goals

`Domain story` stays a few sentences naming the core entities, linking to the chapter 12 glossary for definitions rather than restating them. `Stakeholders` is a `Role | Concern` table, one row per stakeholder — no process detail.

#### 2. Constraints

Each constraint is a terse bullet under `Architecture`, linking an ADR where one exists instead of re-arguing the decision. `Documentation` only points at `doc/arc42/AGENTS.md` for the arc42/Obsidian/OpenSpec precedence rules — it does not restate them.

#### 3. Context and Scope

The PlantUML C4 diagram is the authoritative technical context. Text below it only names which `openspec/specs/*` capability is normative for a choice the diagram shows — it does not restate that capability's content.

#### 4. Solution Strategy

A solution strategy is system-wide, hard-to-reverse decision. A decision that isn't strategic doesn't get a place here — apply the removal test.
Each solution strategy is a bullet point with a very short bird's-eye description without implementation details, and a mandatory ADR xref.

#### 5. Building Block View

Stays exactly one level deep: one `Module | Responsibility` table row per top-level module (`lib/*`, `app/*`), responsibility in a few words — traceability into `doc/concept` is named, not elaborated.

#### 6. Runtime View

Currently an intentional stub (title only) — no runtime scenarios are documented yet.

#### 7. Deployment View

Currently an intentional stub (title only) — no deployment topology is documented yet.

#### 8. Cross-cutting Concepts

Each concept is a heading with a very short bird's-eye description without implementation details — traceability into `doc/concept` is named, not elaborated.

#### 9. Architecture Decisions

Index only: one `ADR | Decision | Status` table row per decision. If the change adds or changes a decision, update this row (decision, status, link) — full content stays in `doc/arc42/adr/`, never duplicated into the chapter.

#### 10. Quality Requirements

A quality goal is a system-wide, hard-to-reverse goal. A goal that isn't strategic doesn't get a place here — apply the removal test.
Each quality goal is a heading with a very short bird's-eye description without implementation details, and a mandatory ADR xref. 

#### 11. Risks and Technical Debt

Risks are kept as a table directly in the chapter, no separate files: `Risk | Score | Mitigation`, Score = likelihood × impact (each 1–3), Mitigation may be empty when none is planned. If the change adds or changes a risk, edit its row directly.

#### 12. Glossary

Entity and value-object definitions are `include::`-d from the fact sheet adoc next to each class in `lib/backend-api` — never hand-typed inline. A term without a corresponding class (a cross-cutting architecture term) goes under `Architecture terms` instead, as a hand-written one- or two-sentence definition.

## Validation checklist

- [ ] No sentence added fails the removal test
- [ ] No Spring/Angular/Svelte/JPA/GraphQL implementation-level detail was added directly to the chapter
- [ ] The edited chapter still matches its chapter-specific format rule above
- [ ] Chapter 9/11 index tables (if touched) still only summarize (decision/risk, status, link), full detail stays in Obsidian
- [ ] The chapter's content boundary wasn't widened without reason
- [ ] No file under `plans/` is referenced from the chapter

## Task output

Report which chapter was edited, what was added/changed, and what (if anything) was moved out to a new or existing Obsidian note instead of being inlined.
