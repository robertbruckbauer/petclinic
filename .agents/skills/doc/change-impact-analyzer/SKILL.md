---
name: change-impact-analyzer
description: 'Determine which of arc42, OpenSpec, Obsidian, ADRs, and risks are affected by a change and produce a focused update checklist; use for prompts like "What documentation does this change affect?" or "Check documentation impact of ...".'
---

## Task steps

### Identify the change

Read the diff, PR, or description of the change under review. List the implementation files touched (entity classes, controllers, schema files, client components, build files).

### Map implementation areas to documentation systems

For each touched area, check whether it's referenced by:
- An `openspec/specs/<capability>/spec.md` requirement (per `AGENTS.md`'s "Artifact locations" and `openspec/project.md`'s capability list).
- A `doc/arc42/*.adoc` chapter (per `plans/330.plan.md` §6's content-boundary table).
- An Obsidian note anywhere under `obsidian/` whose content makes a claim about the changed code.
- An ADR (`doc/arc42/adr/`) or a row in `doc/arc42/11-risks-and-technical-debt.adoc`'s risk table whose subject references the changed code.

### Flag potentially stale Obsidian notes

Per `plans/330.plan.md` §1.1, an Obsidian note is a hypothesis about the code. For every note found in the previous step, do not assume it's still accurate just because it wasn't the target of the diff — flag it for verification even if no doc file was touched, since the code it describes changed.

### Produce the update checklist

List, per affected artifact: the file path, why it's affected, and whether it needs an edit (not just a re-read). Do not edit any file yourself — this skill only analyzes and reports.

## Validation checklist

- [ ] Every touched implementation file was checked against all four documentation systems (arc42, OpenSpec, Obsidian, ADRs/risks), not just the most obvious one
- [ ] No Obsidian note referencing changed code was skipped just because the diff didn't touch a doc file
- [ ] The checklist distinguishes "needs an edit" from "should be re-verified, may already be correct"

## Task output

A checklist grouped by documentation system (arc42 / OpenSpec / Obsidian / ADRs & risks), each entry naming the file and the one-line reason it's affected.
