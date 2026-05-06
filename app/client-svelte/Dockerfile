FROM node:24-alpine@sha256:d1b3b4da11eefd5941e7f0b9cf17783fc99d9c6fc34884a665f40a06dbdfc94f
ENV TZ=Europe/Vienna
RUN apk add --no-cache tzdata
RUN npm install -g pnpm
WORKDIR /workspace
COPY ./pnpm-workspace.yaml ./
COPY ./package.json ./
COPY ./pnpm-lock.yaml ./
COPY ./app/client-angular/package.json ./app/client-angular/
COPY ./app/client-svelte/package.json ./app/client-svelte/
COPY ./app/tester/package.json ./app/tester/
RUN pnpm install --frozen-lockfile
COPY ./app/client-svelte/ ./app/client-svelte/
WORKDIR /workspace/app/client-svelte
RUN pnpm run build

FROM nginx:1.26-alpine@sha256:1eadbb07820339e8bbfed18c771691970baee292ec4ab2558f1453d26153e22d
ENV TZ=Europe/Vienna
EXPOSE 5050
COPY --from=0 /workspace/app/client-svelte/build/generated /usr/share/nginx/html
COPY ./app/client-svelte/nginx.conf /etc/nginx/conf.d/default.conf
