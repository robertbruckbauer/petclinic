# Agent instructions for `doc/arc42/`

## Role of this directory

`doc/arc42/` is the bird's-eye architecture view — for stakeholders, and for an architect or developer new to the project.

## How a document is produced or updated

Editing files in `doc/arc42/` is a documentation change. It never includes editing `.java`, `.js`, `.ts`, or Gradle files — those are separate, implementation-scoped changes that a spec update may motivate but does not itself perform.

Do not add chapters. The structure of `doc/arc42/` is well defined and final.

If removing a detail would not change a stakeholder's understanding of the architecture or an architect's fundamental implementation choices, that detail probably belongs not in an chapter of this documentation.
