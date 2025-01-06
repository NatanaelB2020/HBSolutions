# Etapa 1: Build do Angular App
FROM node:18 as build

# Definir o diretório de trabalho no contêiner
WORKDIR /app

# Copiar os arquivos do projeto Angular
COPY ./package*.json ./

# Instalar dependências
RUN npm install

# Copiar o restante do código do projeto
COPY ./ ./

# Fazer o build do projeto Angular
RUN npm run build --prod

# Etapa 2: Servir os arquivos do Angular com NGINX
FROM nginx:stable-alpine

# Copiar os arquivos compilados do Angular para o diretório do NGINX
COPY --from=build /app/dist/hbsolution-web /usr/share/nginx/html

# Expor a porta do NGINX
EXPOSE 80

# Comando para iniciar o servidor NGINX
CMD ["nginx", "-g", "daemon off;"]
