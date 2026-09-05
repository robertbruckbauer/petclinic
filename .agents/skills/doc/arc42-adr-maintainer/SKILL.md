---
name: arc42-adr-maintainer
description: 'Create or update an architecture decision record and link it from arc42 chapter 9; use for prompts like "Record a decision about ..." or "Update ADR ...".'
---

## Task preconditions

You MUST NOT generate an ADR if even one of the preconditions is not met.

### Identify target decision

Extract the decision's short title from the request.
For a new ADR, find the next unused number in `doc/arc42/adr/` and check no existing ADR already covers the same decision (if one does, this is an update, not a new ADR).
Replace placeholder `{NNNN}-{title}` with the derived file name `doc/arc42/adr/{NNNN}-{title}.adoc`.

## Task steps

### Write or update the ADR body

One AsciiDoc file per decision, `= ADR {NNNN}: {Title}` as the document title, sections as `==`, in order: Status (`proposed`, `accepted`, `deferred`, or `superseded`), Context, Decision, Alternatives considered, Consequences, Links. A `proposed`/`deferred` ADR records open questions instead of a Decision — do not fill in a decision that hasn't actually been made.

### Link to affected arc42 chapters and OpenSpec capabilities

In the Links section, reference the arc42 chapter(s) this decision affects (`link:../{NN}-{chapter}.adoc[]`, relative to `doc/arc42/adr/`) and any `openspec/specs/{capability}/spec.md` it constrains.

### Link from arc42 chapter 9

Add or update the ADR's row in `doc/arc42/09-architecture-decisions.adoc`'s index table (decision, status, link only — full content stays in the ADR file itself, never duplicated into the chapter).

### Refer the ADR from the Obsidian knowledge graph, do not duplicate it there

The ADR lives only under `doc/arc42/adr/` — never create or keep a copy of its content as an Obsidian note. Any Obsidian note that discusses the same decision's rationale, detail, or an open question it raises must **link to** the ADR (e.g. `` `doc/arc42/adr/{NNNN}-{title}.adoc` `` in its body, or a relative path in its `related:` frontmatter) rather than restate the decision.

### Cross-link a superseding decision

If this ADR supersedes an earlier one, set the earlier ADR's status to `superseded` and add a link to the new one — do not delete the old ADR (it stays as a historical record).

## Validation checklist

- [ ] Status is one of `proposed`, `accepted`, `deferred`, `superseded`
- [ ] A `proposed`/`deferred` ADR has no Decision filled in prematurely
- [ ] arc42 chapter 9's index table reflects this ADR's current status
- [ ] No Obsidian note under `obsidian/` contains a copy of this ADR's content — only links to it
- [ ] A superseded ADR is marked `superseded`, not deleted, and links to its replacement

## Task output

Report the ADR's path, its status, whether chapter 9's index was updated, and which Obsidian notes (if any) were updated to link to it instead of restating it.
