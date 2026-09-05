---
name: openspec-maintainer
description: 'Maintain openspec/ as the normative behavior contract via the propose-apply-merge workflow; use for prompts like "Add a requirement for ..." or "Update the OpenSpec capability for ...".'
---

## Task preconditions

You MUST NOT generate a spec change if even one of the preconditions is not met.

### Identify target capability

Extract the capability name from the request (e.g. `rest-conventions`, `owner-management`).
Check that `openspec/specs/{capability}/spec.md` exists, or that a new capability is explicitly being requested.
Replace placeholder `{capability}` with the given name.

### Identify change scope

Determine whether this is a wording fix (edit `specs/{capability}/spec.md` directly) or a behavior change (requires the full propose → apply → merge workflow below).
Check against `plans/330.plan.md` §1.1: if the request would make `openspec` contradict `doc/arc42` or `doc/concept` about current-state fact, stop and reconcile toward those first — do not let this skill silently override them.

## Task steps

### Draft the change (behavior changes only)

Create the working (gitignored, never committed) folder `openspec/changes/{change-id}/` with `proposal.md` (what and why), `tasks.md` (steps), and a delta `specs/{capability}/spec.md` using `## ADDED Requirements` / `## MODIFIED Requirements` / `## REMOVED Requirements` headers, per `openspec/AGENTS.md`.

### Apply the change

Merge the delta directly into `openspec/specs/{capability}/spec.md`, keeping the `### Requirement:` / `#### Scenario:` format already used in every capability spec.

### Discard the working folder

Delete `openspec/changes/{change-id}/`. Do not leave it committed — there is no `changes/archive/`; the commit that changed `specs/` is the record.

### Keep the capability technology-independent

Do not add Spring/Angular/Svelte implementation detail to a requirement. Add operation-specific scenarios only when requested; do not speculatively cover every edge case a capability could have.

### Flag an untracked forward-looking gap

If the requested requirement describes a target ahead of what's implemented (like `security`'s baseline), only accept it when the request also points to (or creates) an ADR under `doc/arc42/adr/` + a row in `doc/arc42/11-risks-and-technical-debt.adoc`'s risk table tracking the gap — otherwise stop and ask for one, per `openspec/AGENTS.md`.

## Validation checklist

- [ ] The capability spec still uses only `### Requirement:` / `#### Scenario:` (Given/When/Then) structure
- [ ] No Spring/Angular/Svelte implementation detail was added to a requirement
- [ ] `openspec/changes/{change-id}/` was deleted after merging, not left in the working tree
- [ ] Any forward-looking (not-yet-implemented) requirement has a tracked ADR + a row in chapter 11's risk table

## Task output

Report which capability was changed, whether it was a direct edit or a full propose/apply/merge cycle, and the ADR path / risk table row name if the change was forward-looking.
