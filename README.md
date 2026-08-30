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
... (199 linhas)
