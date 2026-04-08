#!/bin/sh
dir=$(dirname "$0")
cd $dir
cmd=${1:-install}
npm $cmd && npm run prettierCheck && npm run build && npm run test
