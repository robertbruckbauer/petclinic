---
name: arc42-risk-maintainer
description: 'Add or update a row in arc42 chapter 11''s risk table; use for prompts like "Record a risk about ..." or "Update the mitigation for risk ...".'
---

## Task preconditions

You MUST NOT generate a risk table edit if even one of the preconditions is not met.

### Identify target risk

Extract the risk's name from the request.
Check `doc/arc42/11-risks-and-technical-debt.adoc`'s table for an existing row with the same or an equivalent name (if one exists, this is an update to that row, not a new one).
Risks use **no numbering** — a risk is identified only by its name in the Risk column.

### Determine likelihood and impact

Extract or estimate likelihood and impact, each on a 1 (low) – 3 (high) scale, grounded in what was actually verified about the system (read the relevant code/config first — do not guess). Compute `Score = likelihood × impact`.

## Task steps

### Add or update the table row

`doc/arc42/11-risks-and-technical-debt.adoc` holds exactly one table, columns `Risk | Score | Mitigation`:

- **Risk** — the risk's name, no ID prefix.
- **Score** — formatted as `{score} ({likelihood} × {impact})`, e.g. `3 (1 × 3)`.
- **Mitigation** — leave empty if none is planned yet, or state what mitigates it (may reference an ADR under `doc/arc42/adr/` or an OpenSpec capability).

There is no separate file per risk — everything lives in this one table. Do not create a risk note under `obsidian/`.

### Cross-link an ADR, if the mitigation is a planned future change

If the mitigation references a decision not yet made, point at its ADR (`doc/arc42/adr/{NNNN}-{title}.adoc`) rather than restating the decision's content in the Mitigation cell.

## Validation checklist

- [ ] The risk has no numeric ID — only a name in the Risk column
- [ ] Score is written as `{score} ({likelihood} × {impact})` with both factors in 1–3
- [ ] Mitigation is either empty or names a concrete mitigation/ADR, not a vague placeholder
- [ ] No risk exists as a separate file anywhere in `obsidian/` or elsewhere — the table in chapter 11 is the only copy
- [ ] Likelihood/impact were grounded in something actually checked, not guessed

## Task output

Report the risk's name, its score, and whether this was a new row or an update to an existing one.
