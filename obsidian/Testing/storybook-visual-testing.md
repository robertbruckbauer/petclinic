---
category: Testing
related:
  - test-pyramid.md
  - ../Frontend/client-generation-from-openspec.md
status: current
updated: 2026-09-05
---

# Storybook (planned — standalone client repos only)

Storybook does **not** exist in this repository, and is not planned for `app/client-angular`/`app/client-svelte` — it's scoped to the future standalone repos (`petclinic-angular-ui`, `petclinic-svelte-ui`): one story per lister/editor/viewer component per entity, used to verify styling (daisyUI theme, layout) independent of backend availability. Do not add Storybook config to the in-repo clients based on this note.
