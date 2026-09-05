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

For each candidate sentence: would removing it change a stakeholder's understanding of the architecture, or an architect's fundamental implementation choice? If not, it belongs in `obsidian/` instead — write it there (via `knowledge-maintainer`) and link to it from the chapter, don't inline it.

### Edit the chapter

Keep the chapter's existing content boundary (see `plans/330.plan.md` §6's table for what each of the 12 chapters covers). Add or update only what's architecturally significant. Do not add Spring/Angular/Svelte/JPA/GraphQL implementation detail — link to `obsidian/Backend/` or `obsidian/Frontend/` for that.

### Update the ADR/risk index chapters when applicable

If the change adds or changes a decision, update chapter 9's index table (decision, status, link) — full content stays in `doc/arc42/adr/`, never duplicated into the chapter. If it adds or changes a risk, edit its row directly in chapter 11's `Risk | Score | Mitigation` table — risks have no separate file to keep in sync with.

## Validation checklist

- [ ] No sentence added fails the removal test
- [ ] No Spring/Angular/Svelte/JPA/GraphQL implementation-level detail was added directly to the chapter
- [ ] Chapter 9/11 index tables (if touched) still only summarize (decision/risk, status, link), full detail stays in Obsidian
- [ ] The chapter's content boundary (per `plans/330.plan.md` §6) wasn't widened without reason

## Task output

Report which chapter was edited, what was added/changed, and what (if anything) was moved out to a new or existing Obsidian note instead of being inlined.
