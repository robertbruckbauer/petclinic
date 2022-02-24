FROM node:22-alpine
ENV TZ=Europe/Vienna
RUN apk add --no-cache tzdata
WORKDIR /client
COPY ./package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.26-alpine
ENV TZ=Europe/Vienna
EXPOSE 5000
COPY --from=0 /client/build/generated /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
