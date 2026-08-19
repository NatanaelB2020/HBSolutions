# HBSolutions

Este repositório reúne a aplicação backend em Java/Spring Boot e a interface web em Angular, com foco em gestão comercial e CRM orientado ao fluxo de leads, oportunidades, clientes, atividades e dashboard operacional.

A estrutura principal do projeto é:

- `HbSolution/` — backend Java + Spring Boot + PostgreSQL
- `HBSolution-web/` — frontend Angular
- `docker-compose.yml` — banco PostgreSQL local via Docker

## 1. Visão geral da aplicação

A aplicação evoluiu para um sistema de gestão comercial com características de CRM, incluindo:

- autenticação e autorização via JWT
- gestão de usuários, empresas, clientes e endereços
- cadastro e qualificação de leads
- pipeline de oportunidades por etapa
- fechamento de vendas e controle de status
- atividades e alertas de oportunidade parada
- dashboard com indicadores e resumo de desempenho
- importação de leads por CSV
- integração com Swagger/OpenAPI para documentação da API

O backend usa uma base de dados PostgreSQL e segue um padrão com campos comuns de auditoria e empresa/usuário em `BaseEntity`, além de filtros de empresa e status ativo.

## 2. Stack tecnológica

### Backend

- Java 17
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (jjwt)
- PostgreSQL
- Springdoc OpenAPI / Swagger
- Maven
- Lombok

### Frontend

- Angular 19
- TypeScript
- RxJS
- Angular CLI

### Infraestrutura local

- Docker Compose
- PostgreSQL em container

## 3. Estrutura do projeto

```text
HBSolutions/
├── docker-compose.yml
├── README.md
├── HbSolution/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── src/
│   │   ├── main/java/com/api/HbSolution/
│   │   ├── main/resources/application.properties
│   │   └── test/java/com/api/HbSolution/
│   └── target/
└── HBSolution-web/
    ├── package.json
    ├── angular.json
    ├── src/
    └── public/
```

## 4. Principais módulos e entidades

### Backend

Os principais pacotes do backend incluem:

- `controller/` — endpoints REST
- `service/` — regras de negócio
- `entity/` — entidades JPA
- `repository/` — acesso ao banco
- `security/` — autenticação e JWT
- `exception/` — tratamento global de erros
- `DTO/` — payloads de entrada/saída
- `enums/` — enums do domínio

### Entidades principais

- `UsuarioEntity` — usuários do sistema
- `EmpresaEntity` — empresas/tenants
- `ClienteEntity` — clientes finais
- `LeadEntity` — leads e prospecções
- `OportunidadeEntity` — oportunidades de venda
- `AtividadeEntity` — tarefas e ações do CRM
- `PedidoEntity` — pedido/fechamento comercial
- `ProdutoEntity` — catálogo de produtos
- `EnderecoEntity` — endereço de clientes/empresas

### Enums relevantes

- `StatusLead`
- `OrigemLead`
- `StatusOportunidade`
- `EtapaOportunidade`
- `StatusAtivo`

## 5. Funcionalidades implementadas até o momento

### Autenticação e segurança

- login com JWT
- autenticação por token nas rotas protegidas
- `SecurityConfig` configurando regras de acesso
- `GlobalExceptionHandler` para respostas padronizadas em `ApiError`

### CRM e vendas

- cadastro e busca de leads
- cálculo automático de score do lead
- regra de atribuição de lead ao vendedor com menor carga
- conversão de lead em oportunidade
- pipeline por etapa
- fechamento de oportunidade com ganho/perda
- criação automática de cliente e pedido quando a oportunidade vence
- relatório de vendas por período
- alertas de oportunidades paradas
- dashboard de métricas operacionais

### Operações úteis

- importação de leads via CSV
- busca filtrada por status, origem e score
- minhas oportunidades e meus leads
- painel de atividades e pendências
- organização por empresa (`empresaId`) e usuário logado

## 6. Configuração do ambiente

### Requisitos

- Java 17+
- Maven 3.9+
- Node.js 18+
- npm
- Docker Desktop / Docker Engine
- PostgreSQL (opcional se usar container)

### Variáveis de ambiente

Copie o arquivo de exemplo para um arquivo local real antes de rodar a aplicação:

```bash
cp HbSolution/.env.example HbSolution/.env
```

Edite o arquivo `HbSolution/.env` com os valores reais do ambiente local:

```env
SPRING_DATASOURCE_PASSWORD=sua_senha_aqui
JWT_SECRET=sua_chave_secreta_aqui_minimo_32_caracteres
SPRING_DATASOURCE_USERNAME=hbsolutionadmin
```

A configuração principal do backend está em `HbSolution/src/main/resources/application.properties` e agora exige variáveis sem fallback sensível:

```properties
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/HBsolution}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:hbsolutionadmin}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

> Se o Spring Boot falhar ao iniciar sem essas variáveis, isso é comportamento esperado. É melhor falhar na inicialização do que rodar com senha ou segredo público.

Exemplo de execução em desenvolvimento:

```bash
export SPRING_DATASOURCE_PASSWORD=dev123
export JWT_SECRET=minha_chave_dev_1234567890
./mvnw spring-boot:run
```

Ou diretamente em uma linha:

```bash
export SPRING_DATASOURCE_PASSWORD=dev123 && ./mvnw spring-boot:run
```

## 7. Como subir a aplicação

### 7.1 Subir o banco PostgreSQL via Docker

Antes de subir o banco, certifique-se de que o ambiente local tenha as variáveis preenchidas. O projeto usa o arquivo `HbSolution/.env` para isso.

```bash
cp HbSolution/.env.example HbSolution/.env
# edite o arquivo com os valores reais

cd c:/Desenvolvimento/HBSolutions
docker compose --env-file HbSolution/.env up -d --build
```

Ou, se preferir, exporte as variáveis antes do comando:

```bash
export SPRING_DATASOURCE_USERNAME=hbsolutionadmin
export SPRING_DATASOURCE_PASSWORD=dev123

docker compose up -d --build
```

O arquivo `docker-compose.yml` define o serviço PostgreSQL em:

- host: `localhost`
- porta: `5432`
- banco: `HBsolution`
- usuário: `hbsolutionadmin`
- senha: configurada via `SPRING_DATASOURCE_PASSWORD`

### 7.2 Subir o backend Spring Boot

No diretório do backend:

```bash
cd c:/Desenvolvimento/HBSolutions/HbSolution
./mvnw spring-boot:run
```

Ou, se preferir:

```bash
mvn spring-boot:run
```

A API fica disponível em:

```text
http://localhost:8080/api
```

### 7.3 Subir o frontend Angular

No diretório do frontend:

```bash
cd c:/Desenvolvimento/HBSolutions/HBSolution-web
npm install
npm start
```

A interface web fica disponível em:

```text
http://localhost:4200
```

## 8. Documentação da API (Swagger)

A documentação Swagger/OpenAPI está habilitada no backend.

URL:

```text
http://localhost:8080/api/swagger-ui.html
```

Também é possível consultar os endpoints OpenAPI em:

```text
http://localhost:8080/api/v3/api-docs
```

## 9. Fluxo de uso básico

### 1) Login

A autenticação ocorre em:

```http
POST /api/auth/login
```

Exemplo de payload:

```json
{
  "email": "usuario@empresa.com",
  "senha": "senha123"
}
```

A resposta retorna um token JWT que deve ser enviado no header:

```http
Authorization: Bearer <token>
```

### 2) Visualizar dashboard

```http
GET /api/dashboard
```

### 3) Leads

```http
GET /api/leads/busca
GET /api/leads/meus
POST /api/leads/importar
POST /api/leads/{id}/atualizar-score
POST /api/leads/{id}/converter
```

### 4) Oportunidades

```http
GET /api/oportunidades/pipeline
GET /api/oportunidades/resumo
GET /api/oportunidades/alertas
GET /api/oportunidades/relatorio/vendas
POST /api/oportunidades/{id}/fechar
PATCH /api/oportunidades/{id}/etapa
```

### 5) Clientes e outras áreas

```http
GET /api/clientes
GET /api/empresas
GET /api/produtos
GET /api/usuarios
```

## 10. Endpoints e regras importantes

### Segurança

- rotas públicas: `/auth/**`, `/swagger-ui/**`, `/api-docs/**`
- demais endpoints protegidos por token JWT

### CRM/lead

- o lead possui score automático
- conversão para oportunidade exige que o lead esteja qualificado
- oportunidades abertas podem ser movidas por etapa
- fechamento com status de ganho/perda exige validações específicas
- perdida precisa de motivo
- fechamento ganho pode criar cliente e pedido

### Base de dados e auditoria

- `BaseEntity` centraliza campos como `empresaId`, `usuarioId`, `ativo`, timestamps e regras de persistência
- a aplicação foi estruturada para trabalhar com múltiplas empresas e controle de ativação lógica

## 11. Testes e validações

O backend possui testes unitários para regras de negócio principais, incluindo:

- `LeadServiceTest`
- `OportunidadeServiceTest`
- `BaseServiceTest`
- testes MVC de controller

Validação executada com sucesso na sessão atual:

```bash
cd /c/Desenvolvimento/HBSolutions/HbSolution
mvn -q -Dtest=LeadControllerTest,OportunidadeControllerTest test
```

Resultado observado:

```text
EXIT:0
```

O projeto também configura o plugin JaCoCo para geração de relatório de cobertura.

## 12. Observações importantes

- O backend usa `server.servlet.context-path=/api`, então os endpoints têm este prefixo automaticamente.
- O projeto tem autenticação JWT e, em ambiente de desenvolvimento, a senha padrão do banco e o secret do JWT podem ser ajustados via variáveis de ambiente.
- O front-end Angular se comunica com a API em `localhost:8080` via configuração de proxy/camada de serviço, dependendo da implementação do projeto.
- O banco Postgres foi validado localmente via Docker Compose.

## 13. Status atual do projeto

O projeto está em estado funcional para uso local de desenvolvimento e já conta com:

- autenticação e autorização,
- estrutura de CRM com leads e oportunidades,
- dashboard e alertas,
- importação de dados,
- documentação Swagger,
- persistência com PostgreSQL,
- testes de integração e controller validados.

## 14. Próximos passos sugeridos

- ajustar o fluxo de autenticação no frontend com login completo
- mapear telas de cadastro e edição de clientes/leads/oportunidades
- revisar regras de negócio finais e permissões por perfil
- expandir cobertura de testes para endpoints e cenários de edge-case
- configurar ambiente de produção com variáveis seguras e banco dedicado

## 15. Comandos rápidos de referência

### Backend

```bash
cd c:/Desenvolvimento/HBSolutions/HbSolution
./mvnw spring-boot:run
mvn test
mvn verify
```

### Frontend

```bash
cd c:/Desenvolvimento/HBSolutions/HBSolution-web
npm install
npm start
npm run build
```

### Banco

```bash
cd c:/Desenvolvimento/HBSolutions
docker compose up -d --build
docker compose down
```

---

Se quiser, no próximo passo posso transformar este documento em um guia mais profissional para apresentação ao cliente, com seções de arquitetura, fluxo de usuário, regras de negócio e roadmap do CRM.
