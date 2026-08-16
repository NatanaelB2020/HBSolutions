# HBSolutions – Contexto para IA gratuita

## Visão geral
Este projeto já está evoluindo como uma aplicação de gestão comercial e CRM, com a base funcional estabelecida e foco em manter a estrutura atual, sem reescrever tudo do zero.

Composição atual:

- Backend em Java + Spring Boot
- Frontend em Angular
- Banco PostgreSQL em Docker
- Estrutura com BaseEntity, segurança JWT, scoping por empresa/usuário e fluxo comercial de leads, clientes e oportunidades
- Foco em produtividade comercial e processo de vendas

## Estrutura principal

- `HbSolution/` — backend Java
- `HBSolution-web/` — frontend Angular
- `docker-compose.yml` — ambiente local com PostgreSQL
- `deployment/` — estudos e orientação de arquitetura/deploy
- `README.md` — documentação geral

## Objetivo do projeto
Transformar a aplicação em um CRM comercial funcional com:

- autenticação e autorização via JWT
- gestão de leads e qualificação
- pipeline de oportunidades por etapa
- fechamento de vendas com ganho/perda
- atividades e alertas
- dashboard operacional e KPI
- clientes, empresas, produtos e pedidos
- relatórios comerciais e visão de performance

## Stack atual

### Backend
- Java 17
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Springdoc OpenAPI / Swagger
- Maven
- Lombok
- Apache Commons CSV

### Frontend
- Angular 19
- TypeScript
- RxJS
- Angular CLI
- Angular CDK
- Canvas API para gráfico

### Infraestrutura
- Docker Compose
- PostgreSQL em container local
- Backend em `localhost:8080/api`
- Frontend em `localhost:4200`

## Entidades e módulos principais já existentes

### Entidades principais
- `BaseEntity` — campos base de empresa, usuário, ativo e auditoria
- `UsuarioEntity`
- `EmpresaEntity`
- `ClienteEntity`
- `LeadEntity`
- `OportunidadeEntity`
- `AtividadeEntity`
- `PedidoEntity`
- `ProdutoEntity`
- `EnderecoEntity`

### Enums principais
- `StatusAtivo`
- `StatusLead`
- `OrigemLead`
- `StatusOportunidade`
- `EtapaOportunidade`

### Camadas implementadas

#### Backend
- controllers REST
- services com regras de negócio
- repositories JPA
- security e JWT
- DTOs
- exception handling global
- relatórios e dashboard
- regras de conversão de lead em oportunidade
- importação de leads em CSV

#### Frontend
- login
- dashboard
- menu lateral
- leads list e formulário
- importação CSV de leads
- pipeline de oportunidades
- detalhe da oportunidade
- fechamento da oportunidade
- edição da oportunidade
- atividades
- clientes
- toast global
- gráfico de barras com Canvas
- fluxo por rota e navegação do CRM

## Funcionalidades já prontas ou em estado estável

### CRM e vendas
- cadastro e busca de leads
- cálculo de score do lead
- atribuição de lead ao vendedor com menor carga
- conversão de lead em oportunidade
- pipeline por etapa
- movimentação de oportunidade entre etapas
- fechamento de oportunidade com ganho ou perda
- relatórios de vendas por período
- dashboard com métricas operacionais
- clientes e histórico comercial
- alertas de oportunidade parada

### Operações e usabilidade
- importação de leads por CSV
- filtros por status, origem e score
- lista de meus leads e minhas oportunidades
- atividades do dia e atrasadas
- feedback visual com toasts
- fluxo de edição e detalhe com navegação por rota

## Estado atual do projeto
O projeto está em uma fase de consolidação funcional. A base de CRM já foi montada e os principais fluxos de negócio estão integrados entre backend e frontend.

O foco atual é manter a arquitetura atual, reduzir retrabalho e evoluir a UX e a lógica comercial sem quebrar o que já funciona.

## Observações importantes

- O backend usa `server.servlet.context-path=/api`.
- A comunicação frontend-backend ocorre em `localhost:8080`.
- O Angular roda em `localhost:4200`.
- O banco PostgreSQL local é executado via Docker Compose.
- A aplicação foi ajustada para autenticação JWT e roteamento seguro.
- O build do frontend foi validado com sucesso após a implementação do pipeline e componentes CRM.

## Validações já executadas

- build do Angular concluído com sucesso
- Docker Compose do banco rodando
- estrutura do CRM funcional em desenvolvimento local
- fluxos principais de backend/frontend alinhados
- pipeline e componentes de dashboard/alerta/toast já incorporados

## O que ainda precisa ser revisado

- regras finais de permissões por perfil
- padronização de alguns fluxos de cliente e pedido
- cenário de histórico de cliente
- refinamento da UX em formulários críticos
- aumento da cobertura de testes
- revisão de regras de negócio de conversão e fechamento em produção
- aprimoramento de dark mode e visual geral

## Prompt para IA gratuita

"Analise este projeto como uma aplicação Java + Spring Boot + Angular focada em CRM comercial. A base já está implementada, então o objetivo é evoluir com mínimo retrabalho, sem reescrever tudo do zero. Revise as entidades, services, controllers, repositories, DTOs e componentes