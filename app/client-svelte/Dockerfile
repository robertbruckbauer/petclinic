FROM node:26-alpine@sha256:c6ad9e9c1d80f6eb607c26b4b0a132ff7a2344fad1c78ea3baa2a7d4c8de6d94
ENV TZ=Europe/Vienna
RUN apk add --no-cache tzdata
WORKDIR /client
COPY ./package.json ./
COPY ./package-lock.json ./
COPY .npmrc ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.26-alpine@sha256:1eadbb07820339e8bbfed18c771691970baee292ec4ab2558f1453d26153e22d
ENV TZ=Europe/Vienna
EXPOSE 5050
COPY --from=0 /client/build/generated /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
