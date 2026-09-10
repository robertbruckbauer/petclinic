---
name: obsidian-maintainer
description: 'Create or update an Obsidian knowledge note, verifying its claims against the current code first; use for prompts like "Add a knowledge note about ..." or "Update the note explaining ...".'
---

## Task preconditions

You MUST NOT generate or edit a note if even one of the preconditions is not met.

### Decide whether this task actually warrants a note

Ask explicitly: *Has this task taught me something that a future session would have to rediscover?* A note is owed if any of these triggers applied during the task:

- Reading or searching through several files was needed to answer **a single** question.
- Documentation, a comment, or an existing note turned out to be incorrect or out of date.
- A boundary condition, invariant, or limit surfaced that wasn't documented anywhere.
- One approach was chosen over another, and the reason is worth remembering.
- A pitfall, unexpected behavior, or a non-obvious dependency was encountered.
- History had to be reconstructed (why something was removed, renamed, or replaced).

If none of these apply — the task was routine and nothing here would need rediscovering — do not create or edit a note; report "no note warranted" and stop. This applies whether the request explicitly asked for a note or this skill was invoked generically at the end of a task.

### Check the content actually belongs in the graph

`obsidian/AGENTS.md` is a directory-level agent-instructions file, not a knowledge note.

Only the following kinds of content belong in `obsidian/` — if what triggered the note above doesn't fit one of these, it belongs in a different artifact (arc42/OpenSpec/ADR/risk/code comment) instead, not here:

- Subject-specific rules, invariants, and lifecycles that do not stem from a single file (i.e. not already stated in a fact sheet or code comment — link to that instead of duplicating it).
- Technical limitations and constraints: what **must not** be done, and why.
- Decisions, including **rejected alternatives** and the reasoning behind them (an architecturally significant decision is an ADR instead — see `arc42-adr-maintainer` — this is for smaller, code/pattern-level decisions that don't rise to that level).
- Non-obvious pitfalls, error scenarios, and special cases.
- Conventions.

### Identify target note and category

`obsidian/AGENTS.md` is exempt from the frontmatter, category, and MOC-linkage rules below.

Extract the note's topic and one of the nine allowed categories (Domain, Architecture, Backend, Frontend, Security, Database, Testing, Building, Context) from the request.
Check that the category is one of those nine — no tenth category may be invented.
Replace placeholder `{category}` and `{topic}` accordingly; the file is `obsidian/{category}/{topic}.md`. Architecture decisions and risks are **not** created here — decisions live under `doc/arc42/adr/` (see `arc42-adr-maintainer`) and risks live as rows in `doc/arc42/11-risks-and-technical-debt.adoc`'s table (see `arc42-risk-maintainer`); this skill only links to them.

### Verify the claim against the code

Before writing or editing the note's content, locate the code (or `openspec`/`doc/arc42`/`doc/concept`) it will describe and read it. Every note is a hypothesis about the code, never an independent fact — never write a note's factual claim from memory or assumption.

## Task steps

### Add the note to its category's MOC in the same change

Every category has a map of contents at `obsidian/{category}/moc.md`, linked from the vault index `obsidian/index.md`. In the same change that creates a note (or substantially changes what it's about), add or update a one-line entry for it in that `moc.md`: a concise link plus a short description — never a copy of the note's content. Do not create a new `moc.md`; the nine already exist, one per allowed category. A note is not done until it is reachable from its category's MOC.

### Write or update the note body

State the fact/pattern/rationale concisely — one fact per note, small and atomic. Use standard Markdown links only (no `[[wikilinks]]`). Never restate `openspec/`, `doc/arc42/`, `doc/concept/`, `doc/manual/`, or `doc/service/` content — link to it instead.

### Set frontmatter

```yaml
---
category: {category}
related:
  - {relative link to a related note}
status: current
updated: {today's date}
---
```

### Resolve a contradiction, if the note already existed and disagreed with the code

Follow the code. Correct the note's body and set `updated:` to today. If verification isn't possible (the agent can't read/run the relevant code, or the answer is genuinely ambiguous), set `status: stale` and add a one-line explanation of why — do not guess.

### Never treat a stale or superseded note as current guidance

If this task encounters a note already marked `status: stale` or `status: superseded` while writing a *different* note, do not cite its content as if it were verified — go check the code (or a more authoritative artifact per the precedence order) instead.

## Validation checklist

- [ ] At least one rediscovery trigger actually applied — a note was not written just because this skill was invoked
- [ ] The note's content is one of: subject rules/invariants/lifecycles not tied to a single file, technical must-not constraints, a decision (incl. rejected alternatives), a non-obvious pitfall/error scenario, or a convention
- [ ] Frontmatter has `category`, `related`, `status`, and `updated:` (not applicable to `obsidian/AGENTS.md`, which is exempt)
- [ ] `category` is one of the nine allowed categories
- [ ] The note is linked from its category's `moc.md`, added in this same change
- [ ] Every link is standard Markdown, no `[[wikilinks]]`
- [ ] The note does not restate `openspec/`, `doc/arc42/`, `doc/concept/`, `doc/manual/`, or `doc/service/` content
- [ ] No file under `plans/` is referenced from the note
- [ ] Every factual claim was checked against the code (or the more authoritative artifact) before writing, per the §1.1 precedence order
- [ ] A note that couldn't be verified is `status: stale` with a one-line reason, not a guess presented as fact

## Task output

If no trigger applied: report "no note warranted" and nothing else. Otherwise report the note's path, its `status`, which trigger(s) prompted it, the `moc.md` entry added/updated for it, and — if a contradiction with existing code was found and resolved — a one-line summary of what changed and why.
