FROM node:24-alpine@sha256:8a32c8cf649a49915a66e81e7a609223c708d0adabf288ee3f4077d765542f85
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
