---
name: skill-creator
description: 'Create a new skill under .agents/skills using the structure already established there; use for prompts like "Create a skill for ..." or "Add a skill that ...".'
---

## Task preconditions

You MUST NOT generate a skill file if even one of the preconditions is not met.

### Identify target skill name

Extract the skill's purpose from the request and derive a name.
Check the name is lower-case with hyphens only, following the naming shapes below.
Check that NO skill directory `.agents/skills/{skill-name}` already exists.
Replace placeholder `{skill-name}` with the derived name.

## Task steps

### Create the skill directory and file

Create `.agents/skills/{skill-name}/SKILL.md`.

### Author the front matter

```yaml
---
name: {skill-name}
description: '{one sentence describing what the skill does}; use for prompts like "..." or "...".'
---
```

- `name` must equal the directory name exactly.
- `description` is non-empty and always wrapped in single quotes, per the skill-file checklist in `AGENTS.md`.

### Author "Task preconditions"

Open with the fixed line used by every sampled skill that has this section:

> You MUST NOT generate code if even one of the preconditions is not met.

Adapt the verb if the skill doesn't generate code, e.g. "You MUST NOT generate a skill file if ...".

Add one `###` subsection per extracted input: extract the value from the request, validate it, define its placeholder substitution.

### Author "Task steps"

One `###` subsection per artifact touched.

Each subsection:
- Names the concrete file via its placeholder, e.g. `Create entity class {Entity}.java`.
- Points to the implementation baseline, if one exists.
- States what to add/change as short imperative sentences, not prose paragraphs.
- Applies YAGNI explicitly for optional behavior: "Add operation `X` only when requested."

### Author an optional "Validation checklist"

A flat `- [ ]` list stating what must hold after the skill runs.

### Author an optional "Task output"

State exactly what summary the skill must produce at the end — don't leave the report format implicit.

## Validation checklist

- [ ] Front matter has `name` and `description`; `description` is non-empty and wrapped in single quotes
- [ ] `name` matches the directory name exactly; directory name is lower-case with hyphens
- [ ] Section order is: front matter → (optional) Task preconditions → Task steps → (optional supplementary rule sections) → (optional) Validation checklist → (optional) Task output
- [ ] No section is left empty.
- [ ] Does not duplicate an existing skill; if the requested behavior overlaps an existing skill by more than a small delta, propose extending that skill instead of creating a new one.

## Task output

Report the path of the new `SKILL.md`.
Show open questions, if any section couldn't be filled in confidently from the request alone.
