---
category: context
related:
  - backend-development.md
  - client-generation.md
  - testing.md
status: current
updated: 2026-09-15
---

# Entry point: security

Minimum reading order for an agent asked anything touching authentication/authorization/CORS:

1. The risk table in `doc/arc42/11-risks-and-technical-debt.adoc` — "No authentication or authorization is currently enforced" is a tracked, accepted risk, not an oversight.
2. `obsidian/security/cors-configuration.md` — the one piece of access-control-adjacent code that does exist today.

Roles, scopes, and JWT validation are not specified anywhere in this repository today. If asked to implement JWT/roles: stop and ask the user for the requirements rather than guessing an authorization model.
