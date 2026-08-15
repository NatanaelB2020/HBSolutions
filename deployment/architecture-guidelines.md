# Diretrizes de arquitetura: SOLID e Clean Code

Resumo com sugestões práticas para aplicar SOLID e Clean Code no backend Java (Spring) e frontend Angular.

Principais recomendações (backend):

- Organizar por feature (package por feature) em vez de por camada rígida.
- Controller: apenas orquestração HTTP -> chamar casos de uso (`service` / `usecase`).
- Service / UseCase: camada de aplicação com regras de orquestração.
- Repository: interfaces (Spring Data) para persistência; use DTOs para comunicação entre camadas.
- Entidades: mantê-las enxutas; evitar lógica de negócio complexa nelas.
- Dependência inversa: use interfaces para dependências externas (repositórios, clients) e injete implementações.
- Single Responsibility: cada classe com responsabilidade única.
- Open/Closed: favor composição e interfaces para permitir extenso sem modificar código existente.
- Tests: escrever testes unitários para serviços e testes de integração com Testcontainers para o Postgres.

Padrões e boas práticas (Angular):

- Modularizar por feature (módulos lazy-loaded quando aplicável).
- Componentes finos: componente apenas UI; lógica em serviços.
- Services: injeção via `providedIn: 'root'` ou no módulo; interface para contratos importantes.
- Evitar manipulação direta do DOM; preferir bindings.
- Nomes claros e consistentes para arquivos e símbolos.

Checks automáticos (sugestão):

- Backend: Spotless + Checkstyle + PMD. Configurar no `pom.xml` ou como GitHub Action.
- Frontend: ESLint com regras do Angular e convenções do time.

Estrutura de pastas sugerida (backend):

- `com.example.hbsolution`
  - `config`
  - `api` (controllers / dto)
  - `service` (use cases)
  - `domain` (entities)
  - `repository` (interfaces)
  - `infra` (implementações, clients, adapters)
  - `security`

Inclua este documento no repositório para referência e adapte as regras ao seu time.
