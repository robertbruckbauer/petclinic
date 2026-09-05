---
name: knowledge-maintainer
description: 'Create or update an Obsidian knowledge note, verifying its claims against the current code first; use for prompts like "Add a knowledge note about ..." or "Update the note explaining ...".'
---

## Task preconditions

You MUST NOT generate or edit a note if even one of the preconditions is not met.

### Identify target note and category

Extract the note's topic and one of the eight allowed categories (Domain, Architecture, Backend, Frontend, Security, Database, Testing, Building, Context) from the request.
Check that the category is one of those eight — no ninth category may be invented.
Replace placeholder `{category}` and `{topic}` accordingly; the file is `obsidian/{category}/{topic}.md`. Architecture decisions and risks are **not** created here — decisions live under `doc/arc42/adr/` (see `arc42-adr-maintainer`) and risks live as rows in `doc/arc42/11-risks-and-technical-debt.adoc`'s table (see `arc42-risk-maintainer`); this skill only links to them.

### Verify the claim against the code

Before writing or editing the note's content, locate the code (or `openspec`/`doc/arc42`/`doc/concept`) it will describe and read it. Per `plans/330.plan.md` §1.1, never write a note's factual claim from memory or assumption.

## Task steps

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

- [ ] Frontmatter has `category`, `related`, `status`, and `updated:`
- [ ] `category` is one of the eight allowed categories
- [ ] Every link is standard Markdown, no `[[wikilinks]]`
- [ ] The note does not restate `openspec/`, `doc/arc42/`, `doc/concept/`, `doc/manual/`, or `doc/service/` content
- [ ] Every factual claim was checked against the code (or the more authoritative artifact) before writing, per the §1.1 precedence order
- [ ] A note that couldn't be verified is `status: stale` with a one-line reason, not a guess presented as fact

## Task output

Report the note's path, its `status`, and — if a contradiction with existing code was found and resolved — a one-line summary of what changed and why.
