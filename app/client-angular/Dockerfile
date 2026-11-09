FROM node:26-alpine@sha256:8996bb5821d62a8fd9bb27c1cb07462f3fdce6e47d640c6a881ead0b064ce216
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
EXPOSE 5052
COPY --from=0 /client/build/generated/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
