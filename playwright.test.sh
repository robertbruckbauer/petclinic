#!/bin/sh
dir="$(cd "$(dirname "$0")" && pwd)"

healthz() {
    url=http://localhost:$1/healthz
    max=10
    for i in $(seq 1 $max); do
        if curl -fs $url 2>&1 > /dev/null; then
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
    docker compose --project-name petclinic --file compose.yml up --detach --no-build server client-svelte
    healthz 8080
    healthz 5050
    echo "System is up"
}

composeDown() {
    cd $dir/app/deploy
    docker compose --project-name petclinic --file compose.yml down
    echo "System is down"
}

playwright() {
    cd $dir/app/client-svelte
    PLAYWRIGHT_HTML_REPORT=$dir/pages/html/client-svelte/playwright \
    PLAYWRIGHT_HTML_OPEN=never \
    npx playwright test --reporter=list,html --trace=off --retries=1
}

if composeUp; then
  trap 'composeDown' EXIT
  playwright
fi
