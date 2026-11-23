---
name: arc42-debt-maintainer
description: 'Add or update a row in the debt register of arc42 chapter 11; use for prompts like "Record a debt about ...".'
---

## Task preconditions

You MUST NOT modify the debt register edit if even one of the preconditions is not met.

### Identify target debt

Extract the debt's name from the request.
Check the debt register in `arc42/11-risks-and-debt/debts.adoc`'s table for an existing row with the same or an equivalent name.
If one exists, this is an update to that row, not a new one.

## Task steps

### Create or update the table row

Debts use **no numbering** — a debt is identified only by its name in the Debt column.

The debt register in `arc42/11-risks-and-debt/debts.adoc` holds debts in the level 2 section `Debts` in a table with columns `Debt | Description`:

- **Debt** — the debt's name.
- **Description** — debt description.

There is no separate file per debt — everything lives in this one table.
Do not create a debt note under `obsidian/`.

## Task output

Report the debt's name, and description.
