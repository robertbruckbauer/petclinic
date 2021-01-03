FROM node:alpine
WORKDIR /client
COPY ./package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx
EXPOSE 5000
COPY --from=0 /client/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
