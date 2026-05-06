#!/bin/sh
dir=$(dirname "$0")
cd $dir
cmd=${1:-install}
if [ "$cmd" = "ci" ]; then
  pnpm install --frozen-lockfile
else
  pnpm $cmd
fi
pnpm run prettierCheck && pnpm run build && pnpm run test
