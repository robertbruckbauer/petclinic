---
name: arc42-adr-maintainer
description: 'Create or update an architecture decision record and link it from arc42 chapter 9; use for prompts like "Record a decision about ..." or "Update ADR ...".'
---

## Task preconditions

You MUST NOT generate an ADR if even one of the preconditions is not met.

### Identify target decision

Extract the decision's short title from the request.
For a new ADR, find the next unused number in `arc42/09-architecture-decisions/` and check no existing ADR already covers the same decision (if one does, this is an update, not a new ADR).
Replace placeholder `{NNNN}-{title}` with the derived file name `arc42/09-architecture-decisions/{NNNN}-{title}.adoc`.

## Structure template

Every ADR MUST follow this section order and metadata table.
Keep this template as the source of truth when creating or updating ADRs.

- File starts with `:relrootdir: ../..`
- level 1 section: `ADR {NNNN}: {Title}`
- Metadata table (immediately after the title):

| Field | Value |
| --- | --- |
| Status | `proposed` \| `rejected` \| `accepted` \| `deprecated` \| `superseded` \| `discarded` \| `deferred` |
| Created at | `YYYY-MM-DD` |
| Updated at | `YYYY-MM-DD` |
| Decided by | list of people |

- level 2 section: `Context and problem statement` — why the decision is needed and what problem must be solved
- level 2 section: `Considered options` — every serious option, including the chosen one; each option is a level 3 section with a plain title (no `Option A:` / `Option B:` prefixes); option-local trade-offs belong here
- level 2 section: `Decision` — which option was chosen and why; for `proposed` or `deferred`, record open questions or intended direction; for `discarded`, state that no decision was made; for `rejected`, state the explicit decision against the ADR's suggestion
- level 3 section under Decision: `Consequences` — decision-level outcomes as flat prose or bullets; no Advantages / Constraints subheadings

Do not add a dedicated Links or Status section.
Cross-references to chapters, ADRs, openspec, or obsidian may appear inline where they clarify context or the decision, using `xref:{relrootdir}/arc42/...` (and `link:../../...` when needed).

### Status meanings

| Status | Meaning |
| --- | --- |
| `proposed` | Decision is under discussion; not yet accepted |
| `accepted` | Decision is in force |
| `rejected` | Explicit decision against doing what the ADR suggests |
| `deprecated` | ADR is no longer relevant, and was not replaced by another ADR |
| `superseded` | Replaced by a later ADR (keep the file; link to the replacement) |
| `discarded` | Topic was not important enough; no decision was made and the ADR was dropped |
| `deferred` | Decision postponed; revisit later |

### Metadata rules

- **Status**: exactly one of the values above (lowercase).
- **Created at** / **Updated at**: ISO dates (`YYYY-MM-DD`). On create, both are today. On update, set **Updated at** to today; leave **Created at** unchanged (derive from git history when migrating older files).
- **Decided by**: list of people involved (authors / deciders). Derive from context or git history when unknown.

## Task steps

### Write or update the ADR body

One AsciiDoc file per decision under `arc42/09-architecture-decisions/`, following the structure template above.
For a new ADR, also add `include::09-architecture-decisions/{NNNN}-{title}.adoc[leveloffset=+1]` to `arc42/index.adoc` in numeric order with the other ADRs (book doctype; leveloffset nests the ADR under part 9).

### Link from arc42 chapter 9

Add or update the ADR's row in `arc42/09-architecture-decisions/overview.adoc`'s decision index.
Columns: `ADR | Title | Status` — full content (including Created at, Updated at, Decided by) stays in the ADR file itself, never duplicated into the overview.
Title is the ADR document title (e.g. `ADR 0001: Self-Contained System`).
ADR column xref: `xref:{relrootdir}/arc42/09-architecture-decisions/{NNNN}-{title}.adoc[{NNNN}]`.

### Refer the ADR from the Obsidian knowledge graph, do not duplicate it there

The ADR lives only under `arc42/09-architecture-decisions/` — never create or keep a copy of its content as an Obsidian note.
Any Obsidian note that discusses the same decision must **link to** the ADR rather than restate the decision.

### Cross-link a superseding decision

If this ADR supersedes an earlier one, set the earlier ADR's status to `superseded`, update its **Updated at**, and point to the new ADR in Decision or Consequences — do not delete the old ADR.

## Validation checklist

- [ ] Document starts with `:relrootdir: ../..`, then level 1 section `ADR {NNNN}: {Title}`, then the metadata table
- [ ] Status is one of `proposed`, `rejected`, `accepted`, `deprecated`, `superseded`, `discarded`, `deferred`
- [ ] Metadata table has Status, Created at, Updated at, Decided by in that order
- [ ] Section order is: Context and problem statement → Considered options (with option level 3 sections) → Decision → Consequences
- [ ] No dedicated Links or Status section; no Advantages / Constraints subheadings under Consequences
- [ ] Option level 3 titles have no `Option A:` / `Option B:` prefixes
- [ ] `overview.adoc` index row matches this ADR's Title and Status
- [ ] A superseded ADR is marked `superseded`, not deleted, and references its replacement when one exists
- [ ] New ADR is `include::`-d from `arc42/index.adoc` with `leveloffset=+1`
- [ ] `:relrootdir:` is present and xrefs use `{relrootdir}/arc42/...`
- [ ] No file under `plans/` is referenced from the ADR

## Task output

Report the ADR's path, its status, whether chapter 9's index was updated, and which Obsidian notes (if any) were updated to link to it instead of restating it.
