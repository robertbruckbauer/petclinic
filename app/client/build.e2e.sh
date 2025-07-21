#!/bin/sh
dir=$(dirname "$0")
cd $dir
max=10
for i in $(seq 1 $max); do
    if curl -fs http://localhost:8080/healthz 2>&1 > /dev/null; then
        if curl -fs http://localhost:5000/healthz 2>&1 > /dev/null; then
            npx playwright install --with-deps && npx playwright test
            exit $?
        fi
    fi
    sleep `expr $max - $i + 1`
done
exit 2
