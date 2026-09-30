# Stage 1: Build
FROM node:20-alpine AS build
# ARG muss nach FROM stehen, sonst ist es in dieser Stage nicht sichtbar.
# Werte: production | azure | openshift | docker  (siehe angular.json)
ARG BUILD_CONFIG=production
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration "$BUILD_CONFIG"

# Stage 2: Serve with unprivileged nginx (runs as non-root, OpenShift restricted SCC compatible)
FROM nginxinc/nginx-unprivileged:alpine
COPY --from=build /app/dist/banking-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8080
