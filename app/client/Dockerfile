FROM node:22-alpine@sha256:9f3ae04faa4d2188825803bf890792f33cc39033c9241fc6bb201149470436ca
ENV TZ=Europe/Vienna
RUN apk add --no-cache tzdata
WORKDIR /client
COPY ./package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.26-alpine@sha256:1eadbb07820339e8bbfed18c771691970baee292ec4ab2558f1453d26153e22d
ENV TZ=Europe/Vienna
EXPOSE 5000
COPY --from=0 /client/build/generated /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
