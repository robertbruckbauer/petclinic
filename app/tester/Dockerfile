FROM node:24-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY src/main/express/ ./src/main/express/
# for the volume in compose.yml
RUN mkdir -p build
EXPOSE 9090
CMD ["node", "src/main/express/server.js"]
