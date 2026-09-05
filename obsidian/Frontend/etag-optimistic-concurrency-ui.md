---
category: Frontend
related:
  - rxjs-service-pattern.md
  - ../../openspec/specs/client-shell/spec.md
status: current
updated: 2026-09-05
---

# ETag-based optimistic concurrency in the UI

Because every entity uses ETag/If-Match concurrency (`openspec/specs/rest-conventions/spec.md`), an entity service must carry the ETag it received on load and send it back as `If-Match` on update/delete. A `412 Precondition Failed` response means someone else changed the entity first — `openspec/specs/client-shell/spec.md`'s requirement is that the editor surfaces this as a conflict message with a reload option, never silently retries with a fresh ETag (that would silently discard the user's intended base state) and never silently overwrites.
