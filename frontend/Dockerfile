# syntax=docker/dockerfile:1

# ---------- Stage 1: build the Angular app ----------
FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci --no-audit --no-fund || npm install --no-audit --no-fund

COPY . .

# API base URL baked into the build.
#   - Leave empty (default) when the app is served behind the bundled nginx
#     reverse proxy (same-origin: requests go to /api/... on the same host).
#     This is what docker-compose uses.
#   - Set it to the backend's public URL (e.g. https://api.example.com) when
#     the frontend is hosted separately from the backend (e.g. on Railway).
ARG API_URL=""
RUN printf "export const environment = {\n  production: true,\n  apiUrl: '%s'\n};\n" "$API_URL" \
      > src/environments/environment.prod.ts

RUN npm run build -- --configuration production

# ---------- Stage 2: serve with nginx ----------
FROM nginx:alpine

# Port nginx listens on. Defaults to 80; hosts like Railway override PORT.
ENV PORT=80
# Reverse-proxy target for /api/*. Overridable at runtime.
# Defaults to the compose service name "backend".
ENV BACKEND_ORIGIN=http://backend:8080

# nginx:alpine renders templates in /etc/nginx/templates via envsubst at start.
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/car-rental-angular /usr/share/nginx/html

EXPOSE 80
