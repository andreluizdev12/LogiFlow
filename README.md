# LogiFlow

API para gestão de operações logísticas. A primeira semana entrega a fundação técnica e o ciclo completo de cadastro de clientes.

## Entrega da Semana 1

- Arquitetura em camadas: controller, service, domínio, repository e mapper.
- Cadastro, consulta, listagem, atualização, inativação e reativação de clientes.
- Suporte a pessoa física e jurídica com validação de CPF/CNPJ.
- Normalização de documento, telefone e e-mail.
- Inativação lógica: o cliente não é apagado e continua disponível para consulta.
- Respostas de erro padronizadas com `ProblemDetail` e `traceId`.
- Persistência PostgreSQL versionada pelo Flyway.
- Documentação OpenAPI/Swagger.
- Testes unitários de domínio e service, além de testes HTTP do controller.

## Tecnologias

- Java 21
- Spring Boot 4.1
- Spring Web MVC, Validation e Data JPA
- PostgreSQL e Flyway
- Springdoc OpenAPI
- JUnit 5, Mockito e MockMvc

## Executando localmente

Pré-requisitos: Java 21, Maven e PostgreSQL.

Crie o banco vazio:

```sql
CREATE DATABASE logiflow;
```

Confira a conexão em `src/main/resources/application.yaml` e execute:

```bash
mvn spring-boot:run
```

O Flyway cria e evolui a tabela `client` automaticamente.

## Documentação da API

Com a aplicação iniciada:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints de clientes

| Método | Rota | Operação |
|---|---|---|
| `POST` | `/clients` | Cadastrar cliente |
| `GET` | `/clients/{id}` | Consultar por ID |
| `GET` | `/clients` | Listar clientes |
| `PATCH` | `/clients/{id}` | Atualizar nome, telefone e/ou e-mail |
| `DELETE` | `/clients/{id}` | Inativar cliente |
| `PATCH` | `/clients/{id}/activate` | Reativar cliente |

Exemplo de cadastro:

```json
{
  "externalId": "CLI-001",
  "sourceSystem": "ERP",
  "personType": "PESSOA_FISICA",
  "name": "João da Silva",
  "document": "095.326.265-06",
  "telefone": "(31) 99999-8888",
  "email": "JOAO@EMAIL.COM"
}
```

`externalId`, `sourceSystem`, `personType` e `document` identificam a origem e a identidade do cliente e não são alterados pelo endpoint de atualização.

## Testes

```bash
mvn test
```

A suíte cobre CPF/CNPJ, e-mail, regras do service e os cenários de sucesso e erro de todas as rotas de clientes.

## Artefatos de análise

Os fluxos BPMN estão em `bpmn/` e o diagrama de classes está em `diagramas/`.
