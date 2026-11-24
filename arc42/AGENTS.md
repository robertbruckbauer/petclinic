# Agent instructions for `arc42/`

## Role of this directory

`arc42/` is the bird's-eye architecture view — for stakeholders, and for an architect or developer new to the project.

## How a document is produced or updated

Editing files in `arc42/` is a documentation change.
It never includes editing `.java` or Gradle files — those are separate, implementation-scoped changes that a spec update may motivate but does not itself perform.

Do not add chapters.
The structure of `arc42/` is well defined and final.

If removing a detail would not change a stakeholder's understanding of the architecture or an architect's fundamental implementation choices, that detail probably belongs not in a chapter of this documentation.
