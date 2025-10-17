FROM node:22-alpine@sha256:dbcedd8aeab47fbc0f4dd4bffa55b7c3c729a707875968d467aaaea42d6225af
ENV TZ=Europe/Vienna
RUN apk add --no-cache tzdata
WORKDIR /client
COPY ./package.json ./
COPY ./package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.26-alpine@sha256:1eadbb07820339e8bbfed18c771691970baee292ec4ab2558f1453d26153e22d
ENV TZ=Europe/Vienna
EXPOSE 5050
COPY --from=0 /client/build/generated /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
