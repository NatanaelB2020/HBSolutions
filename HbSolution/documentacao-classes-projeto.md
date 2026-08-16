# Mapa do projeto HBSolution

## Visão geral

Este backend é uma aplicação Spring Boot para gestão de empresa, clientes, usuários, produtos e operações comerciais com foco em CRM. A estrutura principal segue um padrão base com:

- `BaseEntity`: campos comuns e regras de auditoria/multitenancy
- `BaseService`: lógica compartilhada de CRUD e exclusão lógica
- `BaseRepository`: consultas base por empresa e status ativo
- `BaseDTO`: base para transporte de dados

O projeto usa:

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Lombok
- Maven

## Arquitetura base

### Entidade base
Arquivo: `src/main/java/com/api/HbSolution/entity/BaseEntity.java`

Campos principais:
- `id`
- `createdAt`
- `updatedAt`
- `empresaId`
- `usuarioId`
- `ativo` (`StatusAtivo`)

Comportamento:
- `@PrePersist`: define data de criação, empresa e usuário do contexto, e ativa registro
- `@PreUpdate`: atualiza `updatedAt`
- `ativo` é salvo como enum em texto (`ATIVO` / `INATIVO`)

### DTO base
Arquivo: `src/main/java/com/api/HbSolution/DTO/BaseDTO.java`

Campo principal:
- `id`

### Service base
Arquivo: `src/main/java/com/api/HbSolution/service/BaseService.java`

Responsabilidades:
- salvar entidade com empresa e usuário autenticados
- buscar por id, empresa e status ativo
- listar ativos por empresa
- exclusão lógica com `StatusAtivo.INATIVO`

### Repository base
Arquivo: `src/main/java/com/api/HbSolution/repository/BaseRepository.java`

Métodos base:
- `findAllByEmpresaIdAndAtivo(...)`
- `findByIdAndEmpresaIdAndAtivo(...)`

---

## Entidades principais

### 1. EmpresaEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/EmpresaEntity.java`

Campos:
- `nomeFantasia`
- `cnpj`
- `telefone`
- `endereco`
- `usuarios`

Observações:
- representa a organização/tenant da aplicação
- relação com `EnderecoEntity` e lista de `UsuarioEntity`

### 2. UsuarioEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/UsuarioEntity.java`

Campos:
- `nome`
- `email`
- `senha`
- `role`
- `roles`
- `empresa`

Observações:
- usa autenticação e autorização com roles
- tem relacionamento muitos-para-muitos com `RoleEntity`
- mantém `empresaId` e `empresa` para contexto multitenancy

### 3. ClienteEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/ClienteEntity.java`

Campos:
- `nome`
- `cpf`
- `telefone`
- `endereco`

Observações:
- implementa `UsuarioAuditable`
- guarda `usuario` em campo transient para manter compatibilidade com auditoria

### 4. EnderecoEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/EnderecoEntity.java`

Observações:
- usado por `EmpresaEntity`, `ClienteEntity` e outros cadastros
- normalmente representa endereço principal da entidade relacionada

### 5. ProdutoEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/ProdutoEntity.java`

Campos:
- `nome`
- `descricao`
- `preco`
- `quantidadeEstoque`
- `codigoBarras`
- `categoria`

### 6. LeadEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/LeadEntity.java`

Campos:
- `nome`
- `email`
- `telefone`
- `origem` (`OrigemLead`)
- `status` (`StatusLead`)
- `score`
- `observacao`
- `dataConversao`
- `motivoDesqualificacao`
- `lgpdConsentimento`
- `lgpdConsentimentoData`
- `empresa`

Observações:
- representa lead/prospect do CRM
- integra com enums de origem e status

### 7. OportunidadeEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/OportunidadeEntity.java`

Campos:
- `titulo`
- `descricao`
- `etapa` (`EtapaOportunidade`)
- `status` (`StatusOportunidade`)
- `valor`
- `probabilidade`
- `dataFechamentoEstimada`
- `dataFechamentoReal`
- `motivoPerda`
- `alertaAtivo`
- `lead`
- `cliente`
- `usuarioResponsavel`
- `empresa`
- `atividades`
- `pedidos`

Observações:
- núcleo do pipeline comercial
- relacionamento com `LeadEntity`, `ClienteEntity`, `UsuarioEntity`, `AtividadeEntity` e `PedidoEntity`

### 8. AtividadeEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/AtividadeEntity.java`

Campos:
- `titulo`
- `tipo` (`TipoAtividade`)
- `descricao`
- `dataAtividade`
- `status` (`StatusAtividade`)
- `resultado`
- `dataAgendamento`
- `duracaoMinutos`
- `notificacaoEnviada`
- `oportunidade`
- `usuarioResponsavel`

Observações:
- representa tarefas/ações do CRM
- relacionado diretamente à oportunidade

### 9. PedidoEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/PedidoEntity.java`

Campos:
- `cliente`
- `oportunidade`
- `itens`
- `dataPedido`
- `status`
- `valorTotal`
- `observacao`

Observações:
- representa venda/finalização da oportunidade
- tem vínculo com `OportunidadeEntity`

### 10. ItemPedidoEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/ItemPedidoEntity.java`

Observações:
- representa itens dentro de um pedido
- relação com `PedidoEntity` e possível produto

### 11. RoleEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/RoleEntity.java`

Observações:
- entidade de perfil/permissão para usuários

### 12. AdministradorEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/AdministradorEntity.java`

Observações:
- entidade complementar para administração do sistema

### 13. AtendimentoMesaEntity
Arquivo: `src/main/java/com/api/HbSolution/entity/AtendimentoMesaEntity.java`

Observações:
- possivelmente ligado a atendimento e operacionalidade da mesa

---

## DTOs principais

### BaseDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/BaseDTO.java`

Campo:
- `id`

### EmpresaDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/EmpresaDTO.java`

Campos:
- `nomeFantasia`
- `cnpj`
- `telefone`
- `endereco`

### ClienteDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/ClienteDTO.java`

Campos:
- `nome`
- `cpf`
- `email`
- `telefone`
- `endereco`

### UsuarioDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/UsuarioDTO.java`

Observações:
- usado para transferência dos dados do usuário

### ProdutoDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/ProdutoDTO.java`

Observações:
- usado para transporte dos dados de produtos

### LeadDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/LeadDTO.java`

Campos:
- `nome`
- `email`
- `telefone`
- `origem`
- `status`
- `score`
- `observacao`

### OportunidadeDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/OportunidadeDTO.java`

Campos:
- `titulo`
- `descricao`
- `etapa`
- `status`
- `valor`
- `probabilidade`
- `dataFechamentoEstimada`
- `dataFechamentoReal`
- `motivoPerda`
- `alertaAtivo`

### AtividadeDTO
Arquivo: `src/main/java/com/api/HbSolution/DTO/AtividadeDTO.java`

Campos:
- `titulo`
- `tipo`
- `descricao`
- `status`
- `dataAgendamento`
- `duracaoMinutos`
- `resultado`

### EnderecoDTO / EnderecoRequest / EnderecoResponse
Arquivos:
- `src/main/java/com/api/HbSolution/DTO/EnderecoDTO.java`
- `src/main/java/com/api/HbSolution/DTO/EnderecoRequest.java`
- `src/main/java/com/api/HbSolution/DTO/EnderecoResponse.java`

Observações:
- usados para entrada/saída de endereço

### LoginRequestDTO / LoginResponse
Arquivos:
- `src/main/java/com/api/HbSolution/DTO/LoginRequestDTO.java`
- `src/main/java/com/api/HbSolution/DTO/LoginResponse.java`

Observações:
- usados para autenticação via JWT

---

## Serviços principais

### BaseService
Arquivo: `src/main/java/com/api/HbSolution/service/BaseService.java`

Métodos:
- `save(T entity)`
- `findById(Long id)`
- `findAll()`
- `delete(Long id)`
- `delete(T entity)`
- `existsById(Long id)`
- `count()`

### UsuarioService
Arquivo: `src/main/java/com/api/HbSolution/service/UsuarioService.java`

Responsabilidades:
- criptografa senha antes de salvar
- autentica usuário por e-mail e senha
- atualiza status do usuário

### EmpresaService
Arquivo: `src/main/java/com/api/HbSolution/service/EmpresaService.java`

Responsabilidades:
- busca empresas ativas por nome fantasia
- valida existência de CNPJ ativo

### ProdutoService
Arquivo: `src/main/java/com/api/HbSolution/service/ProdutoService.java`

Responsabilidades:
- salva produto usando lógica base
- log simples de operação

### LeadService
Arquivo: `src/main/java/com/api/HbSolution/service/LeadService.java`

Responsabilidades:
- listar leads por empresa
- listar leads ativos por empresa

### OportunidadeService
Arquivo: `src/main/java/com/api/HbSolution/service/OportunidadeService.java`

Responsabilidades:
- listar oportunidades por empresa
- listar oportunidades ativas por empresa
- filtrar por etapa
- filtrar por status

### AtividadeService
Arquivo: `src/main/java/com/api/HbSolution/service/AtividadeService.java`

Responsabilidades:
- listar atividades por empresa
- listar atividades ativas por empresa
- listar atividades por oportunidade
- listar pendentes por usuário responsável

---

## Repositórios principais

### BaseRepository
Arquivo: `src/main/java/com/api/HbSolution/repository/BaseRepository.java`

Métodos base:
- `findAllByEmpresaIdAndAtivo`
- `findByIdAndEmpresaIdAndAtivo`

### LeadRepository
Arquivo: `src/main/java/com/api/HbSolution/repository/LeadRepository.java`

Métodos:
- `findAllByEmpresaId`
- `findAllByEmpresaIdAndAtivo`

### OportunidadeRepository
Arquivo: `src/main/java/com/api/HbSolution/repository/OportunidadeRepository.java`

Métodos:
- `findAllByEmpresaId`
- `findAllByEmpresaIdAndAtivo`
- `findAllByEtapaAndEmpresaIdAndAtivo`
- `findAllByStatusAndEmpresaIdAndAtivo`

### AtividadeRepository
Arquivo: `src/main/java/com/api/HbSolution/repository/AtividadeRepository.java`

Métodos:
- `findAllByEmpresaId`
- `findAllByEmpresaIdAndAtivo`
- `findAllByOportunidadeId`
- `findAllByUsuarioResponsavelIdAndStatusAndAtivo`

---

## Enums do CRM

### StatusAtivo
Arquivo: `src/main/java/com/api/HbSolution/enums/StatusAtivo.java`

Valores:
- `ATIVO`
- `INATIVO`

### OrigemLead
Arquivo: `src/main/java/com/api/HbSolution/enums/OrigemLead.java`

Possíveis valores:
- site
- indicação
- redes_sociais
- whatsapp
- feira
- email
- parceiro
- outros

### StatusLead
Arquivo: `src/main/java/com/api/HbSolution/enums/StatusLead.java`

Possíveis valores:
- NOVO
- EM_CONTATO
- QUALIFICADO
- CONVERTIDO
- DESCARTADO

### EtapaOportunidade
Arquivo: `src/main/java/com/api/HbSolution/enums/EtapaOportunidade.java`

Possíveis valores:
- PROSPECCAO
- QUALIFICACAO
- PROPOSTA
- NEGOCIACAO
- FECHAMENTO
- GANHA
- PERDIDA

### StatusOportunidade
Arquivo: `src/main/java/com/api/HbSolution/enums/StatusOportunidade.java`

Possíveis valores:
- ABERTA
- EM_ANDAMENTO
- GANHA
- PERDIDA
- PAUSADA

### TipoAtividade
Arquivo: `src/main/java/com/api/HbSolution/enums/TipoAtividade.java`

Possíveis valores:
- LIGACAO
- REUNIAO
- EMAIL
- WHATSAPP
- VISITA
- TAREFA
- OUTRO

### StatusAtividade
Arquivo: `src/main/java/com/api/HbSolution/enums/StatusAtividade.java`

Possíveis valores:
- PENDENTE
- EM_ANDAMENTO
- CONCLUIDA
- CANCELADA

---

## API de autenticação e segurança

### SecurityConfig
Arquivo: `src/main/java/com/api/HbSolution/config/SecurityConfig.java`

Observações:
- configura segurança JWT e endpoints públicos

### JwtUtil
Arquivo: `src/main/java/com/api/HbSolution/security/JwtUtil.java`

Observações:
- geração e validação de token JWT

### JwtAuthenticationFilter
Arquivo: `src/main/java/com/api/HbSolution/security/JwtAuthenticationFilter.java`

Observações:
- filtro para autenticar requisições com token

### LoginController / AuthController
Arquivos:
- `src/main/java/com/api/HbSolution/controller/LoginController.java`
- `src/main/java/com/api/HbSolution/controller/AuthController.java`

Observações:
- expõem autenticação de usuários

---

## Controllers existentes

- `UsuarioController`
- `EmpresaController`
- `ClienteController`
- `ProdutoController`
- `EnderecoController`
- `LoginController`
- `AuthController`
- `LeadController`
- `OportunidadeController`
- `AtividadeController`
- `BaseController`

Observações:
- grande parte do padrão segue `@RestController` e endpoints REST básicos para CRUD.
- os controllers de CRM foram adicionados no padrão do projeto para entidades principais do pipeline comercial.

---

## Conclusão

A aplicação já possui uma base sólida para evoluir para CRM, com:

- multitenancy por `empresaId`
- auditoria por `usuarioId`
- exclusão lógica via `StatusAtivo`
- entidades centrais de lead, oportunidade e atividade
- estrutura consistente de serviços, repositorios e DTOs

Portanto, a aplicação pode ser entendida como um sistema de gestão comercial que já está em um estágio avançado de transformação para CRM, sem a necessidade de reescrever a base atual.
