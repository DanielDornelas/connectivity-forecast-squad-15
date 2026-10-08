# Connectivity Forecast API

Reimplementação Java/Spring Boot da [API de referência](https://github.com/LiniiS/connectivity-forecast-api), usada como artefato educacional de APS II.

## Executar a aplicação

### Requisitos

- Java 17
- Maven 3.9 ou superior

Passos

1. Execute os testes para verificar o funcionamento do projeto:
mvn test
2. Inicie a aplicação:
mvn spring-boot:run
3. Acesse a documentação da API no navegador:
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI: http://localhost:8080/v3/api-docs

A aplicação será executada localmente na porta 8080.

## Escopo

API local com catálogo de modelos e previsões mockadas. Fixture igual à referência: 4 modelos ativos, 10 probes e 24 instantes (960 previsões); catálogo também contém modelo inativo. Dados fictícios. Não consulta RIPE Atlas, não treina nem executa modelos, não usa banco de dados e não exige deploy. Classificação e recomendações são regras experimentais, não padrões científicos.

## Estrutura

Separação em controllers, services, repositories, modelos de domínio/DTOs e configuração. API versionada em /api/v1, com recursos de health, modelos, localizações, previsões e atividades.

## Documentação

README e OpenAPI são pontos de partida. Completar em exercício: descrição dos endpoints, parâmetros e validações, exemplos de requisição/resposta, códigos de erro e origem dos campos. Referência à licença MIT da API de origem preservada neste projeto.