---
category: Context
related: []
status: current
updated: 2026-09-05
---

# Entry point: security

Minimum reading order for an agent asked anything touching authentication/authorization/CORS:

1. `openspec/specs/security/spec.md` — the current baseline (no authentication enforced) and the explicit statement that roles/JWT are not yet specified.
2. `doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc` — the open questions blocking that future work; if the request is "add roles" or "add JWT," this ADR must be resolved first, not implemented ad hoc.
3. The risk table in `doc/arc42/11-risks-and-technical-debt.adoc` — the tracked risk this all relates to.
4. `obsidian/Security/cors-configuration.md` — the one piece of access-control-adjacent code that does exist today.

If asked to implement JWT/roles directly: stop and point at ADR 0004's open questions rather than guessing an authorization model.
