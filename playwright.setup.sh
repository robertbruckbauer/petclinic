#!/bin/sh
dir="$(cd "$(dirname "$0")" && pwd)"
cd $dir/app/client-svelte
pnpm exec playwright install chromium --with-deps
