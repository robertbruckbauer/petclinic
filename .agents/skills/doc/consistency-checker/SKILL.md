---
name: consistency-checker
description: 'Structurally and cross-artifact validate arc42, OpenSpec, Obsidian, ADRs, and risks; use for prompts like "Check documentation consistency" or "Run a consistency pass".'
---

## Task steps

### Check Obsidian structural rules

- Every note under `obsidian/` has frontmatter with `category`, `related`, `status`, `updated:`.
- `category` is one of the eight allowed categories; no ninth category directory exists.
- No `[[wikilink]]` syntax appears anywhere — only standard Markdown links.
- No orphan notes (nothing links to them and they link to nothing) and no broken relative links.
- A note with `status: stale` or `status: superseded` carries a one-line explanation and is not cited elsewhere as if it were current guidance.

### Check OpenSpec structural rules

- Every `openspec/specs/{capability}/spec.md` uses only `### Requirement:` / `#### Scenario:` structure.
- No `openspec/changes/` folder is committed to the repository (it must be gitignored working state only).
- No capability spec restates `doc/arc42` or `doc/concept` content instead of linking to it.

### Check arc42 structural rules

- All 12 standard chapters exist under `doc/arc42/`.
- Chapter 9's index table lists every ADR actually present under `doc/arc42/adr/`, and vice versa — no ADR missing from the index, no index row pointing at a missing file.
- Chapter 11's risk table has no numbered risks, every row has a `Score` formatted as `{score} ({likelihood} × {impact})` with both factors in 1–3, and no risk exists as a duplicate file anywhere under `obsidian/`.
- No Obsidian note under `obsidian/` duplicates an ADR's content — a note discussing a decision links to `doc/arc42/adr/{NNNN}-{title}.adoc` rather than restating it.

### Check cross-artifact traceability

- Every OpenSpec requirement that describes a forward-looking (not-yet-implemented) target has a linked ADR + a row in chapter 11's risk table.
- Every ADR/risk links back to at least one arc42 chapter or OpenSpec capability it affects.
- Report, but do not silently resolve, any contradiction found between artifacts — per `plans/330.plan.md` §1.1, report it in precedence order (code/tests, then arc42, then doc/concept, then openspec, then obsidian) rather than picking a side.

## Validation checklist

- [ ] Every check above was actually run, not assumed passing
- [ ] Findings are grouped by documentation system, each with a file path and a one-line reason
- [ ] No finding silently "fixes" a contradiction — it's reported for a human or the appropriate maintainer skill to resolve

## Task output

A findings list grouped by check category (Obsidian structure / OpenSpec structure / arc42 structure / cross-artifact traceability), each entry naming the file(s) and the specific rule violated. An empty list for a category is reported as "checked, no issues" — not omitted.
