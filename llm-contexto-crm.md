# Contexto completo do projeto HbSolution para IA / LLM

## 1. Visão geral
- Projeto: HbSolution
- Stack principal: Java 17 + Spring Boot 3.4.0 + JPA + PostgreSQL + Angular
- Backend: `HbSolution/`
- Frontend: `HBSolution-web/`
- Objetivo atual: evoluir para um CRM mínimo, preservando a estrutura já existente e adicionando o núcleo comercial
- Base de autenticação: JWT
- API base: `/api`
- Swagger: `/swagger-ui.html`

## 2. Configuração atual da aplicação
Arquivo: `HbSolution/src/main/resources/application.properties`

- nome da app: `HbSolution`
- `server.servlet.context-path=/api`
- `server.port=8080`
- banco PostgreSQL: `postgres:5432/HBsolution`
- usuário default: `${SPRING_DATASOURCE_USERNAME:hbsolutionadmin}`
- senha default: `${SPRING_DATASOURCE_PASSWORD:Berlolo@0106}`
- driver: PostgreSQL
- `spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}`
- `spring.jpa.packages-to-scan=com.api.HbSolution.entity`
- `spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL:true}`
- JWT secret: `${JWT_SECRET:defaultSecretKey}`
- JWT expiration: `${JWT_EXPIRATION:86400000}`

## 3. Estrutura do código atual

### Backend
- `com.api.HbSolution.entity`
- `com.api.HbSolution.repository`
- `com.api.HbSolution.service`
- `com.api.HbSolution.controller`
- `com.api.HbSolution.DTO`
- `com.api.HbSolution.security`
- `com.api.HbSolution.enums`
- `com.api.HbSolution.exception`

### Frontend
- `HBSolution-web/src/app`
- componentes principais: empresa, cliente, produto, login, menu

## 4. Entidades JPA existentes

### `BaseEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/BaseEntity.java`

Campos:
- `id`
- `createdAt`
- `updatedAt`
- `empresaId`
- `usuarioId`
- `ativo` (`StatusAtivo`)

Lifecycle:
- `@PrePersist`
- `@PreUpdate`

Regras:
- preenche `empresaId` e `usuarioId` pelo usuário logado
- status padrão é `ATIVO`
- uso de exclusão lógica

### `EmpresaEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/EmpresaEntity.java`

Campos:
- `nomeFantasia`
- `cnpj`
- `telefone`
- `endereco`
- `usuarios`

Relacionamentos:
- `@OneToOne` com `EnderecoEntity`
- `@OneToMany` com `UsuarioEntity`

Objetivo:
- representar a empresa ou unidade de negócio

### `ClienteEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/ClienteEntity.java`

Campos:
- `nome`
- `cpf`
- `telefone`
- `endereco`
- `usuario`

Implementa:
- `UsuarioAuditable`

Objetivo:
- representar cliente ou contato ligado ao sistema

### `UsuarioEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/UsuarioEntity.java`

Campos:
- `nome`
- `email`
- `senha`
- `role`
- `roles`
- `empresa`

Relacionamentos:
- `@ManyToMany` com `RoleEntity`
- `@ManyToOne` com `EmpresaEntity`

Objetivo:
- autenticação, autorização e vínculo com empresa

### `EnderecoEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/EnderecoEntity.java`

Campos:
- `logradouro`
- `numero`
- `bairro`
- `cidade`
- `estado`
- `cep`
- `complemento`

Objetivo:
- endereço genérico reutilizável

### `ProdutoEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/ProdutoEntity.java`

Campos:
- `nome`
- `descricao`
- `preco`
- `quantidadeEstoque`
- `codigoBarras`
- `categoria`

Objetivo:
- cadastro de produto/serviço

### `PedidoEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/PedidoEntity.java`

Campos:
- `cliente`
- `oportunidade`
- `itens`
- `dataPedido`
- `status`
- `valorTotal`
- `observacao`

Relacionamentos:
- `@ManyToOne` com `ClienteEntity`
- `@ManyToOne` com `OportunidadeEntity`
- `@OneToMany` com `ItemPedidoEntity`

Objetivo:
- fechamento de venda ou pedido final

### `ItemPedidoEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/ItemPedidoEntity.java`

Campos:
- `pedido`
- `produto`
- `quantidade`
- `precoUnitario`
- `precoTotal`
- `observacao`
- `usuario`

Objetivo:
- detalhe do pedido

### `AdministradorEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/AdministradorEntity.java`

Campos:
- `nome`
- `email`
- `senha`

Objetivo:
- entidade específica para administrador

### `AtendimentoMesaEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/AtendimentoMesaEntity.java`

Campos:
- `numeroMesa`
- `statusAtendimento`
- `pedido`
- `dataAbertura`
- `dataFechamento`
- `observacao`
- `usuario`

Enums usados:
- `StatusAtendimento`

Objetivo:
- controle de atendimento em mesa

### `RoleEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/RoleEntity.java`

Campos:
- `id`
- `nome`

Objetivo:
- papel/permissão do usuário

## 5. Entidades do CRM adicionadas

### `LeadEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/LeadEntity.java`

Campos:
- `nome`
- `email`
- `telefone`
- `origem`
- `status`
- `score`
- `observacao`
- `empresa`

Objetivo:
- prospect ou lead que entra no funil comercial

### `OportunidadeEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/OportunidadeEntity.java`

Campos:
- `titulo`
- `descricao`
- `etapa`
- `status`
- `valor`
- `probabilidade`
- `dataFechamento`
- `lead`
- `cliente`
- `usuarioResponsavel`
- `empresa`
- `atividades`
- `pedidos`

Objetivo:
- representar o pipeline de vendas

### `AtividadeEntity`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/AtividadeEntity.java`

Campos:
- `titulo`
- `tipo`
- `descricao`
- `dataAtividade`
- `status`
- `oportunidade`
- `usuarioResponsavel`

Objetivo:
- registrar contato, follow-up e acompanhamento do cliente

## 6. Enums existentes

### `StatusAtivo`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/enums/StatusAtivo.java`

Valores:
- `ATIVO`
- `INATIVO`

### `StatusAtendimento`
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/enums/StatusAtendimento.java`

Valores:
- `EM_ANDAMENTO`
- `CONCLUIDO`
- `CANCELADO`

## 7. Interface de usuário auditable
Arquivo: `HbSolution/src/main/java/com/api/HbSolution/entity/UsuarioAuditable.java`

Métodos:
- `setUsuario(UsuarioEntity usuario)`
- `getUsuario()`

Objetivo:
- padronizar entidades que precisam registrar usuário responsável

## 8. Repositórios existentes
- `BaseRepository`
- `ClienteRepository`
- `EmpresaRepository`
- `EnderecoRepository`
- `ProdutoRepository`
- `UsuarioRepository`
- `AtendimentoMesaRepository`

Padrão:
- repositórios estendem `BaseRepository` ou `JpaRepository`
- base repository tem métodos de empresa e status ativo

## 9. Serviços existentes
- `BaseService`
- `ClienteService`
- `EmpresaService`
- `EnderecoService`
- `ProdutoService`
- `UsuarioService`
- `UsuarioDetailsService`
- `AuthService`

Padrão:
- base service cuida de autenticação do usuário logado
- filtro por empresa e status ativo
- exclusão lógica

## 10. Controllers existentes
- `BaseController`
- `ClienteController`
- `EmpresaController`
- `EnderecoController`
- `ProdutoController`
- `UsuarioController`
- `AuthController`
- `LoginController`
- `TestController`

Padrão:
- CRUD genérico em `BaseController`
- autenticação e login em endpoints dedicados

## 11. DTOs existentes
- `BaseDTO`
- `ClienteDTO`
- `EmpresaDTO`
- `EnderecoDTO`
- `EnderecoRequest`
- `EnderecoResponse`
- `ProdutoDTO`
- `UsuarioDTO`
- `AdministradorDTO`
- `AtendimentoMesaDTO`
- `LoginRequestDTO`
- `LoginResponse`

Objetivo:
- separar transporte de dados da entidade JPA

## 12. Segurança e autenticação
Arquivos importantes:
- `JwtUtil.java`
- `JwtAuthenticationFilter.java`
- `SecurityUtils.java`
- `UsuarioDetails.java`

Conceitos:
- JWT para autenticação
- usuário logado via `SecurityContextHolder`
- `SecurityUtils.getUsuarioLogado()` fornece contexto da sessão
- `UsuarioDetails` expõe usuário e permissões

## 13. Regras de negócio globais do sistema
- multi-tenancy por empresa
- quase toda entidade segue filtro por `empresaId`
- exclusão lógica em vez de física
- o usuário logado influencia o `usuarioId` e `empresaId`
- autenticação e autorização são parte central da arquitetura

## 14. Fluxo de CRM sugerido
- `Empresa`
- `Cliente/Contato`
- `Lead`
- `Oportunidade`
- `Atividade`
- `Pedido`

Relação lógica:
- empresa tem clientes e leads
- lead vira oportunidade
- oportunidade recebe atividades
- pedido representa venda fechada vinculada à oportunidade

## 15. Limites e boas práticas para IA
- manter foco em entidades e JPA
- evitar frontend e regras extras enquanto o domínio não estiver estável
- preferir pequenas mudanças em blocos
- manter compatibilidade com `BaseEntity` e `BaseService`
- usar enums para status e etapas quando a estrutura estiver madura

## 16. Observações finais
Este contexto reflete o código atual do projeto e o que já foi adaptado para a base do CRM. O objetivo não é reescrever tudo, mas preservar a estrutura existente e evoluir o núcleo de relacionamento comercial de forma gradual.
