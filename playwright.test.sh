#!/bin/sh
dir="$(cd "$(dirname "$0")" && pwd)"

healthz() {
    url=http://localhost:$1/healthz
    max=10
    for i in $(seq 1 $max); do
        curl -fs $url 2>&1 > /dev/null
        if [ $? -eq 0 ]; then
            echo "$url is healthy"
            return
        fi
        echo "$url is NOT healthy"
        sleep `expr $max - $i + 1`
    done
    exit 2
}

composeUp() {
    cd $dir/app/deploy
    docker compose --project-name petclinic --file compose.yml up --detach --no-build server $*
    if [ $? -eq 0 ]; then
        echo "System is starting"
        healthz 8080
        healthz 5050
        healthz 5052
        echo "System is up"
    else
        echo "System failed to start"
        exit 2
    fi
}

composeDown() {
    cd $dir/app/deploy
    docker compose --project-name petclinic --file compose.yml down
    if [ $? -eq 0 ]; then
        echo "System is down"
    else
        echo "System failed to stop"
        exit 2
    fi
}

playwright() {
    cd $dir/app/$1
    PLAYWRIGHT_HTML_REPORT=$dir/pages/html/$1/playwright \
    PLAYWRIGHT_HTML_OPEN=never \
    npx playwright test --reporter=list,html --trace=off --retries=1
    if [ $? -ne 0 ]; then
        echo "Playwright failed"
        exit 2
    fi
}

if composeUp client-angular client-svelte; then
  trap 'composeDown' EXIT
  playwright client-angular
  playwright client-svelte
fi
