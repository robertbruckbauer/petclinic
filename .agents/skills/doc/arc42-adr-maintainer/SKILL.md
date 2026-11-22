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

Every ADR MUST follow this AsciiDoc shape exactly (heading order).
Keep this template as the source of truth when creating or updating ADRs.

```asciidoc
:relrootdir: ../..
= ADR {NNNN}: {Title}

== Status

{proposed | accepted | deferred | superseded — plus optional short clarifying sentence}

== Context

{Why the decision is needed and what problem must be solved.}

== Decision

{Which option was chosen and why. For proposed/deferred, record open questions instead of inventing a decision.}

== Alternatives considered

- **{Option title}** — {outcome: rejected/deferred/… and why}

== Consequences

- {Outcomes of the decision}

== Links

- xref:{relrootdir}/arc42/{NN}-{chapter}/…[arc42 ch. N: …]
- optional openspec/obsidian references
```

### Status meanings

| Status | Meaning |
| --- | --- |
| `proposed` | Decision is under discussion; not yet accepted |
| `accepted` | Decision is in force |
| `deferred` | Decision postponed; revisit later |
| `superseded` | Replaced by a later ADR (keep the file; link to the replacement) |

### Body rules

- H1 is only the ADR title (`= ADR {NNNN}: {Title}`), preceded by `:relrootdir: ../..`.
- Sections in order: Status → Context → Decision → Alternatives considered → Consequences → Links.
- A `proposed`/`deferred` ADR records open questions instead of a Decision — do not fill in a decision that hasn't actually been made.
- Links use `xref:{relrootdir}/arc42/...` for chapters/ADRs; openspec/root docs may use `link:../../…`.
- Do not reformat existing ADRs into a metadata-table / Considered-options template.

## Task steps

### Write or update the ADR body

One AsciiDoc file per decision under `arc42/09-architecture-decisions/`, following the structure template above.
For a new ADR, also add `include::09-architecture-decisions/{NNNN}-{title}.adoc[leveloffset=+1]` to `arc42/index.adoc` in numeric order with the other ADRs (book doctype; leveloffset nests the ADR under part 9).

### Link from arc42 chapter 9

Add or update the ADR's row in `arc42/09-architecture-decisions/overview.adoc`'s index table.
Columns: `ADR | Decision | Status` — full content stays in the ADR file itself, never duplicated into the overview.
ADR column xref: `xref:{relrootdir}/arc42/09-architecture-decisions/{NNNN}-{title}.adoc[{NNNN}]`.

### Refer the ADR from the Obsidian knowledge graph, do not duplicate it there

The ADR lives only under `arc42/09-architecture-decisions/` — never create or keep a copy of its content as an Obsidian note.
Any Obsidian note that discusses the same decision must **link to** the ADR (e.g. `` `arc42/09-architecture-decisions/{NNNN}-{title}.adoc` `` in its body, or a relative path in its `related:` frontmatter) rather than restate the decision.

### Cross-link a superseding decision

If this ADR supersedes an earlier one, set the earlier ADR's status to `superseded` and add a link to the new one — do not delete the old ADR (it stays as a historical record).

## Validation checklist

- [ ] Status is one of `proposed`, `accepted`, `deferred`, `superseded`
- [ ] A `proposed`/`deferred` ADR has no Decision filled in prematurely
- [ ] A superseded ADR is marked `superseded`, not deleted, and links to its replacement
- [ ] `arc42/09-architecture-decisions/overview.adoc` reflects current status and `xref` of all ADRs.
- [ ] `:includedir:` is present and includes use `{includedir}/...`
- [ ] `:relrootdir:` is present and xrefs use `{relrootdir}/...`
- [ ] No file under `plans/` is referenced from the chapter

## Task output

Report the ADR's path, its status, whether chapter 9's index was updated, and which Obsidian notes (if any) were updated to link to it instead of restating it.
