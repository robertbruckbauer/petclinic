#!/bin/sh
dir="$(cd "$(dirname "$0")" && pwd)"
cd $dir/app/client
version=$(jq -r '.devDependencies["@playwright/test"]' package.json)
echo "version=$version"