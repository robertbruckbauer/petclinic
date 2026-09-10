# Agent instructions for `openspec/`

## Role of this directory

`openspec/` contains the normative, implementable behavior contract for the backend (REST, GraphQL, security) and for every generated client (UI structure and behavior).

## How a document is produced or updated

- A capability with the `-platform` suffix is technology-specific by design, its whole purpose being to pin that module's current technology stack as a requirement rather than abstract away from it. 
- A significant behavior change must not land in code before its `specs/` update — requirements and scenarios are normative, not descriptive-after-the-fact.
- Never restate what `doc/arc42` already say; link to them.
- Never restate what `doc/concept` already say; link to them.
- Never restate what `doc/service` already say; link to them.

Editing files in `openspec/` is a documentation change. It never includes editing `.java`, `.js`, `.ts`, or Gradle files — those are separate, implementation-scoped changes that a spec update may motivate but does not itself perform.
