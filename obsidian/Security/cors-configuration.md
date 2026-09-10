---
category: Security
related:
  - ../../doc/arc42/adr/0004-jwt-resource-server-and-roles.adoc
  - ../../doc/arc42/11-risks-and-technical-debt.adoc
status: current
updated: 2026-09-05
---

# CORS configuration

CORS is implemented in `lib/backend-api/src/main/java/esy/auth/Cors.java`, a small static-helper class consumed by `EsySecurityConfiguration`'s `http.cors(Customizer.withDefaults())`. It fixes: allowed origins (`localhost` variants, `*.cardsplus.info`, `*.github.dev`), credentials allowed, a fixed header/method allowlist, a 3600s max age, and Private Network Access enabled (needed for local development across network boundaries, e.g. devcontainers — see the class's own Javadoc links for background).

This is the *only* access-control-adjacent code in the backend today — CORS is a browser-enforced boundary, not a substitute for authentication. See the risk table in `doc/arc42/11-risks-and-technical-debt.adoc` for what that means, and `openspec/specs/security/spec.md` for the normative statement of this same configuration.
