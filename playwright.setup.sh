#!/bin/sh
dir="$(cd "$(dirname "$0")" && pwd)"
cd $dir/app/client
npx playwright install chromium --with-deps
