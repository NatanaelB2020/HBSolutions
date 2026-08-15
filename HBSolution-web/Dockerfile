# Etapa 1: Build Angular
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY ./ ./
RUN npm run build --prod

# Etapa 2: Nginx
FROM nginx:stable-alpine
COPY --from=build /app/dist/hbsolution-web/browser /usr/share/nginx/html
COPY default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1
CMD ["nginx", "-g", "daemon off;"]
