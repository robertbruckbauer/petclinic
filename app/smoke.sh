#!/bin/sh
dir=$(dirname "$0")
cd $dir

healthz() {
    url=http://localhost:$1/healthz
    max=10
    for i in $(seq 1 $max); do
        if curl -fs $url 2>&1 > /dev/null; then
            return
        fi
        echo "$url is healthy"
        sleep `expr $max - $i + 1`
    done
    exit 2
}

composeUp() {
    (cd deploy; docker compose --project-name petclinic --file compose.yml up --detach --no-build server client)
    healthz 8080
    healthz 5000
    echo "system is up"
}

composeDown() {
    (cd deploy; docker compose --project-name petclinic --file compose.yml down server client)
    echo "system is down"
}

regression() {
    (cd client; npx playwright install $1 --with-deps && npx playwright test --reporter=list)
    echo "regression completed"
}

if composeUp; then 
{
    regression chromium
} && {
    composeDown
    exit 0
} || {
    composeDown
    exit 1
}
fi
