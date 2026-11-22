---
category: building
related:
  - gradle-module-conventions.md
status: current
updated: 2026-11-22
---

# Node tooling conventions

Both clients are Vite-based (`client-angular` via the Angular CLI's Vite-backed builder, `client-svelte` via plain Vite), styled with Tailwind 4 + daisyUI 5, formatted with Prettier (`prettierApply`/`prettierCheck` npm scripts). CI (`.github/workflows/build.yml`) runs Node 24 and invokes each client's own `build.npm.sh ci` script rather than driving `npm`/`vite` directly from the workflow — keeping the exact install/build sequence owned by the client, not the CI config.

## npm 12 `allowScripts`

npm 12 blocks dependency install/postinstall scripts unless the package is listed in that client's `package.json` `allowScripts` field. Without approval, `npm install` still succeeds but skips those scripts — notably `esbuild`'s `postinstall`, which then breaks Vite/`ng build`.

`client-angular` allows only `esbuild` (pinned version) and explicitly denies `@parcel/watcher`, `lmdb`, and `msgpackr-extract`. The watcher ships prebuilds via optional deps (its install script is normally a no-op); LMDB/msgpackr natives are cache performance only. Review with `npm install-scripts ls`, change with `approve`/`deny`, then `npm rebuild` for newly approved packages. Re-approve `esbuild` after its pinned version bumps.
