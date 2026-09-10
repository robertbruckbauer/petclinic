---
category: Building
related:
  - gradle-module-conventions.md
status: current
updated: 2026-09-05
---

# Node tooling conventions

Both clients are Vite-based (`client-angular` via the Angular CLI's Vite-backed builder, `client-svelte` via plain Vite), styled with Tailwind 4 + daisyUI 5, formatted with Prettier (`prettierApply`/`prettierCheck` npm scripts). CI (`.github/workflows/build.yml`) runs Node 24 and invokes each client's own `build.npm.sh ci` script rather than driving `npm`/`vite` directly from the workflow — keeping the exact install/build sequence owned by the client, not the CI config.
