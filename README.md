# Viagens API

API RESTful para gerenciamento de destinos de viagem, desenvolvida como solução ao **Desafio 1 — Desenvolvimento de Sistemas Web** (UniSENAI · ADS · 2026/2).

---

## Sumário

1. [Visão Geral do Problema](#1-visão-geral-do-problema)
2. [Arquitetura Proposta](#2-arquitetura-proposta)
3. [Tecnologias Utilizadas](#3-tecnologias-utilizadas)
4. [Estrutura do Projeto](#4-estrutura-do-projeto)
5. [Endpoints da API](#5-endpoints-da-api)
6. [Como Executar](#6-como-executar)
7. [Exemplos de Teste](#7-exemplos-de-teste)
8. [Funcionalidade de Avaliação](#8-funcionalidade-de-avaliação)
9. [Interface Web](#9-interface-web)

---

## 1. Visão Geral do Problema

Uma agência de viagens em processo de modernização digital necessita de uma **API REST** para centralizar o gerenciamento de seus destinos turísticos e possibilitar integração com aplicativos parceiros, plataformas de turismo e sistemas futuros.

A empresa opera atualmente com um site institucional e um sistema interno de reservas desconectados. A ausência de uma API padronizada impede a integração com parceiros externos e limita a escalabilidade dos serviços digitais.

### Funcionalidades implementadas

- Cadastrar destinos de viagem com nome, localização, descrição e atividades
- Listar todos os destinos disponíveis
- Pesquisar destinos por nome ou localização
- Visualizar detalhes de um destino específico
- Atualizar informações de um destino existente
- Registrar avaliações e recalcular automaticamente a média
- Excluir destinos do sistema

---

## 2. Arquitetura Proposta

A aplicação adota a **Arquitetura em Camadas (Layered Architecture)**, padrão amplamente utilizado em projetos Spring Boot.

```
┌─────────────────────────────────────┐
│         Cliente (HTTP/Browser)      │
└──────────────────┬──────────────────┘
                   │ requisição HTTP
┌──────────────────▼──────────────────┐
│   Controller  (camada web)          │  ← recebe e devolve HTTP
│   DestinoController                 │
└──────────────────┬──────────────────┘
                   │ delega
┌──────────────────▼──────────────────┐
│   Service  (regras de negócio)      │  ← toda lógica fica aqui
│   DestinoService                    │
└──────────────────┬──────────────────┘
                   │ lê/escreve
┌──────────────────▼──────────────────┐
│   Model + Repositório em Memória    │  ← entidade + ConcurrentHashMap
│   Destino, DTOs                     │
└─────────────────────────────────────┘
```

### Responsabilidade de cada camada

| Camada | Responsabilidade |
|---|---|
| **Controller** | Recebe requisições HTTP, valida entrada, delega ao Service e retorna a resposta |
| **Service** | Contém toda a lógica de negócio: cálculo de médias, validações, operações no repositório |
| **Model/Entity** | Define a estrutura dos dados (Destino) e os DTOs de entrada (DestinoRequest, AvaliacaoRequest) |
| **Exception** | Centraliza o tratamento de erros com respostas JSON padronizadas |

### Por que essa arquitetura?

- **Separação de responsabilidades** — cada camada tem uma função específica, facilitando testes e manutenção
- **Evolução independente** — trocar o armazenamento em memória por JPA/banco de dados não exige alterar o Controller
- **Testabilidade** — a DestinoService pode ser testada unitariamente sem subir o servidor HTTP
- **Padrão de mercado** — modelo referência para APIs REST com Spring Boot

---

## 3. Tecnologias Utilizadas

| Tecnologia | Papel | Justificativa |
|---|---|---|
| **Java 8** | Linguagem principal | Amplamente adotado no mercado; compatível com o ambiente de desenvolvimento |
| **Spring Boot 2.7** | Framework principal | Reduz configuração manual; servidor Tomcat embutido; ideal para APIs REST |
| **Spring Web (MVC)** | Camada HTTP | Mapeamento de rotas via anotações (`@RestController`, `@GetMapping`, etc.) |
| **Spring Validation** | Validação de dados | Bean Validation (JSR-380); valida DTOs de entrada sem código manual |
| **Maven** | Gerenciador de build | Gerencia dependências e ciclo de build de forma declarativa via `pom.xml` |
| **ConcurrentHashMap** | Repositório em memória | Thread-safe para ambiente web; múltiplas requisições simultâneas sem conflito |
| **JUnit 5** | Testes unitários | Garante que cada funcionalidade se comporta conforme esperado |

---

## 4. Estrutura do Projeto

```
viagens-api/
├── pom.xml                                        ← dependências e build
├── README.md                                      ← documentação técnica
└── src/
    ├── main/
    │   ├── java/com/agencia/viagens/
    │   │   ├── ViagensApiApplication.java         ← ponto de entrada (@SpringBootApplication)
    │   │   ├── controller/
    │   │   │   └── DestinoController.java         ← endpoints HTTP
    │   │   ├── service/
    │   │   │   └── DestinoService.java            ← lógica de negócio + repositório em memória
    │   │   ├── model/
    │   │   │   ├── Destino.java                   ← entidade principal
    │   │   │   ├── DestinoRequest.java            ← DTO de entrada (cadastro/atualização)
    │   │   │   └── AvaliacaoRequest.java          ← DTO para registrar avaliação
    │   │   └── exception/
    │   │       ├── DestinoNotFoundException.java  ← exceção de recurso não encontrado
    │   │       └── GlobalExceptionHandler.java    ← tratamento centralizado de erros
    │   └── resources/
    │       ├── static/index.html                  ← interface web para testar a API
    │       └── application.properties
    └── test/
        └── java/com/agencia/viagens/service/
            └── DestinoServiceTest.java            ← testes unitários da camada de serviço
```

---

## 5. Endpoints da API

**Base URL:** `http://localhost:8080`

| Método | Rota | Descrição | Corpo | Retorno |
|---|---|---|---|---|
| `GET` | `/destinos` | Lista todos os destinos | — | `200 OK` |
| `GET` | `/destinos?busca={termo}` | Pesquisa por nome ou localização | — | `200 OK` |
| `GET` | `/destinos/{id}` | Retorna um destino específico | — | `200 OK` |
| `POST` | `/destinos` | Cadastra novo destino | JSON | `201 Created` |
| `PUT` | `/destinos/{id}` | Atualiza destino completo | JSON | `200 OK` |
| `PATCH` | `/destinos/{id}/avaliacao` | Registra avaliação e recalcula média | JSON | `200 OK` |
| `DELETE` | `/destinos/{id}` | Remove o destino | — | `204 No Content` |

### Corpo das requisições

**POST `/destinos` e PUT `/destinos/{id}`:**
```json
{
  "nome": "Lisboa",
  "localizacao": "Lisboa, Portugal",
  "descricao": "Capital histórica de Portugal.",
  "atividades": ["Torre de Belém", "Alfama", "Mosteiro dos Jerônimos"]
}
```

**PATCH `/destinos/{id}/avaliacao`:**
```json
{
  "nota": 4.5
}
```
> A nota deve estar entre **1.0** e **5.0**.

### Formato de resposta de erro

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

## 6. Como Executar

### Pré-requisitos

- **Java 8** ou superior (JDK — não JRE)
- **Eclipse IDE** ou outra IDE com suporte a Maven

### Pelo Eclipse (recomendado)

1. Vá em **File → Import → Maven → Existing Maven Projects**
2. Selecione a pasta `viagens-api` e clique em **Finish**
3. Clique com botão direito no projeto → **Run As → Java Application**
4. Selecione **ViagensApiApplication** e confirme
5. Aguarde: `Started ViagensApiApplication in X.XXX seconds`
6. Acesse **http://localhost:8080** no navegador

### Pelo terminal (Maven)

```bash
cd viagens-api
mvn spring-boot:run
```

### Dados de exemplo

A aplicação inicia com **3 destinos pré-cadastrados**:

| ID | Nome | Localização | Média |
|---|---|---|---|
| 1 | Paris | Paris, França | 4.75 |
| 2 | Rio de Janeiro | Rio de Janeiro, Brasil | 4.8 |
| 3 | Kyoto | Kyoto, Japão | 4.9 |

---

## 7. Exemplos de Teste

### Listar todos os destinos
```bash
curl http://localhost:8080/destinos
```

### Pesquisar por termo
```bash
curl "http://localhost:8080/destinos?busca=Paris"
```

### Buscar por ID
```bash
curl http://localhost:8080/destinos/1
```

### Cadastrar destino
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
    "descricao": "A Cidade Luz, descrição atualizada.",
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

## 8. Funcionalidade de Avaliação

A média é calculada de forma incremental sem armazenar o histórico de notas:

```
nova_media = media_anterior + (nota - media_anterior) / total_avaliacoes
```

Isso garante eficiência em memória — apenas dois valores são armazenados (`totalAvaliacoes` e `mediaAvaliacoes`), sem necessidade de uma lista de notas.

---

## 9. Interface Web

A aplicação inclui uma interface web acessível em **http://localhost:8080** que permite testar todos os endpoints visualmente, sem necessidade de ferramentas externas como Postman ou curl.

Funcionalidades disponíveis na interface:
- **Listar** — exibe destinos em cards com estrelas e atividades
- **Buscar por ID** — retorna JSON do destino
- **Pesquisar** — filtra por nome ou localização
- **Cadastrar** — formulário completo
- **Atualizar** — PUT com todos os campos
- **Avaliar** — PATCH com nota de 1.0 a 5.0
- **Excluir** — DELETE com confirmação

---

*Desenvolvido para o Desafio 1 de Desenvolvimento de Sistemas Web — UniSENAI ADS, 2026.*
