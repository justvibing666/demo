# ---- Etap budowania ----
FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---- Etap produkcyjny (Nginx) ----
FROM nginx:1.27-alpine AS runtime

# Skopiuj zbudowany frontend
COPY --from=build /app/dist /usr/share/nginx/html

# Konfiguracja Nginx dla Vue Router (history mode)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
