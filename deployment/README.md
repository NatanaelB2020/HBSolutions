# Deployment com Docker

Arquivos e comandos para executar a aplicação localmente com Docker Compose.

Passos:

1. Copie `.env.sample` para `.env` e ajuste valores sensíveis.

```bash
cp deployment/.env.sample deployment/.env
```

2. Inicie os serviços:

```bash
cd deployment
docker compose up --build
```

4. Testar healthchecks (após `up`):

```bash
# Verificar status dos containers
docker ps

# Verificar logs do backend
docker logs -f hbsolution-backend
```

3. Serviços expostos:
- Backend: http://localhost:8080
- Frontend: http://localhost:80

Observações:
- O `deployment/docker-compose.yml` assume que os `Dockerfile` do backend e frontend estão em `HbSolution/` e `HBSolution-web/` respectivamente.
