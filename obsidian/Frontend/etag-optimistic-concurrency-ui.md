---
category: Frontend
related:
  - rxjs-service-pattern.md
  - ../../openspec/specs/client-shell/spec.md
status: current
updated: 2026-09-08
---

# ETag-based optimistic concurrency in the UI — required by spec, not actually implemented

`openspec/specs/rest-conventions/spec.md` requires ETag/If-Match concurrency, and `openspec/specs/client-shell/spec.md` requires the editor to surface a `412` as a conflict message with a reload option. **Neither client does this today.** `BackendService` (`app/client-angular/.../services/backend.service.ts`, mirrored in Svelte) never reads the `ETag` response header on `GET`, and its `restApiPut`/`restApiPatch`/`restApiDelete` never send an `If-Match` header at all — every entity service (`owner.service.ts`, `pet.service.ts`, `vet.service.ts`, `visit.service.ts`) inherits this, so a `412` can never actually occur from either shipped client; a stale write is silently overwritten instead.

This is an untracked gap between the OpenSpec requirement and the code — not yet an ADR-tracked forward-looking target, per `openspec/AGENTS.md`'s rule that an untracked gap is a defect in the spec, not an acceptable target. Flagged here as a pitfall rather than resolved: fixing it means either implementing the header plumbing in `BackendService`, or amending `client-shell` to stop claiming behavior that doesn't exist.
