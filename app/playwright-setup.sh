#!/bin/sh
dir=$(dirname "$0")
(cd $dir/client; npx playwright install chromium --with-deps)
