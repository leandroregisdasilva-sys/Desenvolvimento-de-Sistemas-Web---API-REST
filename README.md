# Viagens API

API RESTful para gerenciamento de destinos de viagem, desenvolvida como solução ao **Desafio 1 — Desenvolvimento de Sistemas Web** (UniSENAI · ADS · 2026/2).

---

## Sumário

1. [Visão Geral do Problema](#1-visão-geral-do-problema)
2. [Arquitetura Proposta](#2-arquitetura-proposta)
3. [Justificativa das Tecnologias](#3-justificativa-das-tecnologias)
4. [Estrutura do Projeto](#4-estrutura-do-projeto)
5. [Endpoints da API](#5-endpoints-da-api)
6. [Como Executar](#6-como-executar)
7. [Exemplos de Requisições](#7-exemplos-de-requisições)

---

## 1. Visão Geral do Problema

Uma agência de viagens em processo de modernização necessita de uma **API REST** que permita:

- Integração com aplicativos de turismo e parceiros comerciais.
- Gerenciamento centralizado de destinos turísticos (cadastro, consulta, atualização, avaliação e remoção).
- Base de arquitetura escalável para futuras integrações (banco de dados, autenticação, etc.).

Esta primeira versão foca na **definição da arquitetura**, na **organização do código em camadas** e na **implementação dos endpoints principais**, utilizando armazenamento em memória para simplificar a entrega inicial.

---

## 2. Arquitetura Proposta

A API segue o padrão de **arquitetura em camadas** (Layered Architecture), amplamente adotado em projetos Spring Boot:

```
┌─────────────────────────────────┐
│         Cliente (HTTP)          │
└────────────────┬────────────────┘
                 │ requisição
┌────────────────▼────────────────┐
│      Controller (camada web)    │  ← recebe e devolve HTTP
│      DestinoController          │
└────────────────┬────────────────┘
                 │ chama
┌────────────────▼────────────────┐
│      Service (regras de negócio)│  ← toda lógica fica aqui
│      DestinoService             │
└────────────────┬────────────────┘
                 │ lê/escreve
┌────────────────▼────────────────┐
│      Model / Repositório        │  ← entidade + mapa em memória
│      Destino + ConcurrentHashMap│
└─────────────────────────────────┘
```

### Por que essa arquitetura?

| Benefício | Explicação |
|---|---|
| **Separação de responsabilidades** | Cada camada tem uma única função, facilitando testes e manutenção. |
| **Evolução independente** | Trocar o armazenamento em memória por JPA/banco de dados não exige alterar o Controller. |
| **Testabilidade** | A `DestinoService` pode ser testada sem subir o servidor HTTP. |
| **Padrão de mercado** | É o modelo ensinado e exigido em entrevistas técnicas e projetos reais. |

---

## 3. Justificativa das Tecnologias

### Java 17
- Linguagem tipada, com ecossistema maduro para desenvolvimento back-end.
- LTS (Long-Term Support) com suporte garantido até 2029.
- Records, sealed classes e melhorias de switch disponíveis para futuras refatorações.

### Spring Boot 3.3
- Simplifica a criação de aplicações Spring: convenção sobre configuração.
- **Spring Web (Spring MVC)** oferece mapeamento de rotas HTTP via anotações (`@RestController`, `@GetMapping`, etc.).
- **Spring Validation** integra Bean Validation (JSR-380) para validar os dados de entrada sem código manual.
- Servidor embutido (Tomcat): a aplicação é executada como um `.jar` simples, sem necessidade de servidor externo.
- Comunidade enorme, documentação excelente e amplamente adotado no mercado brasileiro.

### Maven
- Gerenciador de dependências e build padrão do ecossistema Java.
- O arquivo `pom.xml` declara todas as dependências de forma explícita e reproduzível.

### Armazenamento em Memória (`ConcurrentHashMap`)
- Atende ao requisito da primeira entrega sem adicionar complexidade de configuração de banco.
- `ConcurrentHashMap` garante thread-safety em ambiente web (múltiplas requisições simultâneas).
- A estrutura da `DestinoService` já está preparada para ser substituída por um `JpaRepository` com mudanças mínimas.

---

## 4. Estrutura do Projeto

```
viagens-api/
├── pom.xml                                        # dependências e configuração de build
├── README.md                                      # documentação técnica
└── src/
    ├── main/
    │   ├── java/com/agencia/viagens/
    │   │   ├── ViagensApiApplication.java         # ponto de entrada (@SpringBootApplication)
    │   │   ├── controller/
    │   │   │   └── DestinoController.java         # endpoints HTTP
    │   │   ├── service/
    │   │   │   └── DestinoService.java            # lógica de negócio + repositório em memória
    │   │   ├── model/
    │   │   │   ├── Destino.java                   # entidade principal
    │   │   │   ├── DestinoRequest.java            # DTO de entrada (cadastro/atualização)
    │   │   │   └── AvaliacaoRequest.java          # DTO para registrar avaliação
    │   │   └── exception/
    │   │       ├── DestinoNotFoundException.java  # exceção de recurso não encontrado
    │   │       └── GlobalExceptionHandler.java    # tratamento centralizado de erros
    │   └── resources/
    │       └── application.properties            # porta, nome da app, formato JSON
    └── test/
        └── java/com/agencia/viagens/service/
            └── DestinoServiceTest.java            # testes unitários da camada de serviço
```

---

## 5. Endpoints da API

Base URL: `http://localhost:8080`

| Método   | Rota                          | Descrição                                      | Status de Sucesso |
|----------|-------------------------------|------------------------------------------------|-------------------|
| `GET`    | `/destinos`                   | Lista todos os destinos                        | `200 OK`          |
| `GET`    | `/destinos?busca={termo}`     | Pesquisa por nome ou localização               | `200 OK`          |
| `GET`    | `/destinos/{id}`              | Retorna detalhes de um destino específico      | `200 OK`          |
| `POST`   | `/destinos`                   | Cadastra um novo destino                       | `201 Created`     |
| `PUT`    | `/destinos/{id}`              | Atualiza completamente um destino              | `200 OK`          |
| `PATCH`  | `/destinos/{id}/avaliacao`    | Registra avaliação e recalcula a média         | `200 OK`          |
| `DELETE` | `/destinos/{id}`              | Remove um destino                              | `204 No Content`  |

### Justificativa dos métodos HTTP

- **GET**: recuperação de dados, sem alteração de estado — idempotente.
- **POST**: criação de um novo recurso; retorna `201` com o recurso criado.
- **PUT**: atualização completa; todos os campos do corpo substituem os anteriores.
- **PATCH**: atualização parcial; altera apenas a avaliação sem tocar nos outros campos.
- **DELETE**: remoção; `204 No Content` confirma sucesso sem corpo de resposta.

### Corpo das requisições

#### POST /destinos e PUT /destinos/{id}
```json
{
  "nome": "Lisboa",
  "localizacao": "Lisboa, Portugal",
  "descricao": "Capital histórica de Portugal, repleta de cultura e fados.",
  "atividades": ["Torre de Belém", "Mosteiro dos Jerônimos", "Alfama"]
}
```

#### PATCH /destinos/{id}/avaliacao
```json
{
  "nota": 4.5
}
```
> A nota deve estar entre **1.0** e **5.0**.

---

## 6. Como Executar

### Pré-requisitos

- **Java 17** ou superior instalado (`java -version`)
- **Maven 3.8+** instalado (`mvn -version`) **ou** usar o wrapper incluso (`./mvnw`)

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/<seu-usuario>/viagens-api.git
cd viagens-api

# 2. Compile e execute
mvn spring-boot:run

# A aplicação estará disponível em:
# http://localhost:8080
```

### Executar os testes

```bash
mvn test
```

### Gerar o JAR executável

```bash
mvn package
java -jar target/viagens-api-1.0.0.jar
```

---

## 7. Exemplos de Requisições

Os exemplos abaixo podem ser executados com **curl**, **Postman**, **Insomnia** ou qualquer cliente HTTP.

### Listar todos os destinos
```bash
curl http://localhost:8080/destinos
```

### Pesquisar destinos
```bash
curl "http://localhost:8080/destinos?busca=Paris"
```

### Buscar destino por ID
```bash
curl http://localhost:8080/destinos/1
```

### Cadastrar novo destino
```bash
curl -X POST http://localhost:8080/destinos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Lisboa",
    "localizacao": "Lisboa, Portugal",
    "descricao": "Capital histórica de Portugal.",
    "atividades": ["Torre de Belém", "Alfama"]
  }'
```

### Atualizar destino
```bash
curl -X PUT http://localhost:8080/destinos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Paris",
    "localizacao": "Paris, França",
    "descricao": "A Cidade Luz, agora com descrição atualizada.",
    "atividades": ["Torre Eiffel", "Louvre", "Versalhes"]
  }'
```

### Avaliar destino
```bash
curl -X PATCH http://localhost:8080/destinos/1/avaliacao \
  -H "Content-Type: application/json" \
  -d '{ "nota": 4.8 }'
```

### Excluir destino
```bash
curl -X DELETE http://localhost:8080/destinos/2
```

---

## Dados de Exemplo

A aplicação já inicia com **3 destinos pré-cadastrados** para facilitar os testes:

| ID | Nome | Localização |
|----|------|-------------|
| 1  | Paris | Paris, França |
| 2  | Rio de Janeiro | Rio de Janeiro, Brasil |
| 3  | Kyoto | Kyoto, Japão |

---

## Tratamento de Erros

Todas as respostas de erro seguem o formato:

```json
{
  "timestamp": "2026-08-30T14:32:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Destino com ID 99 não encontrado."
}
```

| Situação | Status HTTP |
|---|---|
| ID inexistente | `404 Not Found` |
| Campos inválidos ou ausentes | `400 Bad Request` |
| Nota fora do intervalo (1–5) | `400 Bad Request` |
| Erro interno inesperado | `500 Internal Server Error` |

---

*Desenvolvido para o Desafio 1 de Desenvolvimento de Sistemas Web — UniSENAI ADS, 2026.*
