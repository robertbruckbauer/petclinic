# Agent instructions for `openspec/`

## Role of this directory

`openspec/specs/` is the normative, implementable behavior contract for the backend (REST, GraphQL, security) and for every generated client (UI behavior). It ranks below `doc/arc42` and `doc/concept`, and above `obsidian`, in the precedence order defined in `project.md` — see that file before resolving any contradiction.

## Workflow: propose → apply → merge (no persisted change history)

1. **Propose.** For a significant behavior change, draft a change under `openspec/changes/<change-id>/`: `proposal.md` (what and why), `tasks.md` (steps), optionally `design.md` (cross-cutting/architectural changes only), and a delta `specs/<capability>/spec.md` using `## ADDED Requirements` / `## MODIFIED Requirements` / `## REMOVED Requirements` headers.
2. **Review.** Treat this working folder as a normal draft — discuss it, revise it, do not commit it on its own.
3. **Apply.** Once accepted, merge the delta straight into `openspec/specs/<capability>/spec.md`.
4. **Discard.** Delete `openspec/changes/<change-id>/` — it is gitignored and was never meant to persist. The commit that changed `specs/` is the record; there is no `changes/archive/`.

Do not skip step 1 for a small typo fix — edit `specs/` directly for those. Reserve the full workflow for changes that alter *what must be true*, not how it's phrased.

## Rules this directory's maintainer (`knowledge-maintainer`'s sibling, `openspec-maintainer`) enforces

- Specs stay technology-independent where possible — no Spring/Angular/Svelte implementation detail. That belongs in `doc/service/*.adoc` (generated API docs) or `obsidian/Backend|Frontend/` (patterns, rationale).
- A significant behavior change must not land in code before its `specs/` update — OpenSpec is normative, not descriptive-after-the-fact.
- Never restate what `doc/arc42` or `doc/concept` already say; link to them.
- A capability spec may describe a target ahead of the current implementation only when the gap is tracked via an ADR (`doc/arc42/adr/`) + a row in `doc/arc42/11-risks-and-technical-debt.adoc`'s risk table (see `project.md` → "Planned future changes"). An untracked gap is a defect in the spec, not an acceptable target.

## Hard rule: documentation-only changes

Editing `openspec/` is a documentation change. It never includes editing `.java`, `.js`, `.ts`, or Gradle files — those are separate, implementation-scoped changes that a spec update may motivate but does not itself perform.
