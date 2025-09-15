#!/bin/sh
dir=$(dirname "$0")

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
    (cd $dir/deploy; docker compose --project-name petclinic --file compose.yml up --detach --no-build server client)
    healthz 8080
    healthz 5000
    echo "System is up"
}

composeDown() {
    (cd $dir/deploy; docker compose --project-name petclinic --file compose.yml down server client)
    echo "System is down"
}

playwright() {
    (cd $dir/client; npx playwright test --reporter=list)
}

if composeUp; then 
{
    playwright
} && {
    composeDown
    exit 0
} || {
    composeDown
    exit 1
}
fi
