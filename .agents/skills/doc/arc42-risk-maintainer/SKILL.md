---
name: arc42-risk-maintainer
description: 'Add or update a row in the risk register of arc42 chapter 11; use for prompts like "Record a risk about ..." or "Update the mitigation for risk ...".'
---

## Task preconditions

You MUST NOT modify the risk register edit if even one of the preconditions is not met.

### Identify target risk

Extract the risk's name from the request.
Check the risk register in `arc42/11-risks-and-debt/risks.adoc`'s table for an existing row with the same or an equivalent name.
If one exists, this is an update to that row, not a new one.

## Task steps

### Determine likelihood, impact and mitigation

Start a relentless interview to estimate likelihood and impact for the risk score and identify a risk mitigation with the `grilling` skill, each on a 1 (low) – 3 (high) scale.

### Create or update the table row

Risks use **no numbering** — a risk is identified only by its name in the Risk column.

The risk register in `arc42/11-risks-and-debt/risks.adoc` holds risks in the level 2 section `Risks` in a table with columns `Risk | Score | Mitigation`:

- **Risk** — the risk's name.
- **Score** — risk score displayed as `{score} ({likelihood} × {impact})`, e.g. `3 (1 × 3)`.
- **Mitigation** — risk mitigation.

There is no separate file per risk — everything lives in this one table.
Do not create a risk note under `obsidian/`.

## Task output

Report the risk's name, its score, and mitigation.
