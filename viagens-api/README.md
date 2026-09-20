# Viagens API — v2

API RESTful para gerenciamento de destinos de viagem, evoluída com **PostgreSQL**, **Spring Data JPA** e **Spring Security**.

Desenvolvida como solução ao **Desafio 2 — Desenvolvimento de Sistemas Web** (UniSENAI · ADS · 2026/2).

---

## Sumário

1. [Visão Geral](#1-visão-geral)
2. [Tecnologias Utilizadas](#2-tecnologias-utilizadas)
3. [Arquitetura](#3-arquitetura)
4. [Configuração do Banco de Dados](#4-configuração-do-banco-de-dados)
5. [Como Executar](#5-como-executar)
6. [Usuários e Perfis de Teste](#6-usuários-e-perfis-de-teste)
7. [Endpoints e Regras de Acesso](#7-endpoints-e-regras-de-acesso)
8. [Exemplos de Requisições](#8-exemplos-de-requisições)

---

## 1. Visão Geral

Esta versão evolui a API desenvolvida no Desafio 1, substituindo o armazenamento em memória por um banco de dados **PostgreSQL** e adicionando **autenticação e autorização** com Spring Security.

### O que mudou em relação à v1

| Aspecto | v1 | v2 |
|---|---|---|
| Armazenamento | Memória (ConcurrentHashMap) | PostgreSQL |
| Persistência | Temporária | Permanente |
| Autenticação | Nenhuma | HTTP Basic (Spring Security) |
| Autorização | Nenhuma | Perfis ADMIN e USER |
| Acesso a dados | Direto no Service | Via Repository (JPA) |

---

## 2. Tecnologias Utilizadas

| Tecnologia | Versão | Papel |
|---|---|---|
| Java | 8 | Linguagem principal |
| Spring Boot | 2.7.18 | Framework principal |
| Spring Web | 2.7.18 | Camada HTTP e REST |
| Spring Data JPA | 2.7.18 | Persistência e acesso a dados |
| Spring Security | 2.7.18 | Autenticação e autorização |
| Spring Validation | 2.7.18 | Validação de dados de entrada |
| PostgreSQL | 14+ | Banco de dados relacional |
| Flyway | 8.x | Versionamento do esquema do banco |
| Hibernate | 5.6.x | ORM (mapeamento objeto-relacional) |
| BCrypt | — | Hash seguro de senhas |
| Maven | 3.8+ | Gerenciador de dependências e build |

---

## 3. Arquitetura

A aplicação segue a **Arquitetura em Camadas**:

```
┌─────────────────────────────────────────┐
│         Cliente (HTTP/Postman)          │
└──────────────────┬──────────────────────┘
                   │ HTTP + Credenciais (Basic Auth)
┌──────────────────▼──────────────────────┐
│   Spring Security (Autenticação/Autorização) │
└──────────────────┬──────────────────────┘
                   │ se autenticado e autorizado
┌──────────────────▼──────────────────────┐
│   Controller  (camada web)              │
│   DestinoController                     │
└──────────────────┬──────────────────────┘
                   │ delega
┌──────────────────▼──────────────────────┐
│   Service  (regras de negócio)          │
│   DestinoService                        │
└──────────────────┬──────────────────────┘
                   │ acessa dados via
┌──────────────────▼──────────────────────┐
│   Repository  (acesso a dados)          │
│   DestinoRepository (JpaRepository)     │
└──────────────────┬──────────────────────┘
                   │ persiste em
┌──────────────────▼──────────────────────┐
│   PostgreSQL  (banco de dados)          │
└─────────────────────────────────────────┘
```

### Regras de autorização

| Operação | Método | Rota | Perfil necessário |
|---|---|---|---|
| Listar destinos | GET | `/destinos` | Público |
| Buscar por ID | GET | `/destinos/{id}` | Público |
| Pesquisar | GET | `/destinos?busca=` | Público |
| Cadastrar | POST | `/destinos` | ADMIN |
| Atualizar | PUT | `/destinos/{id}` | ADMIN |
| Avaliar | PATCH | `/destinos/{id}/avaliacao` | USER ou ADMIN |
| Excluir | DELETE | `/destinos/{id}` | ADMIN |

---

## 4. Configuração do Banco de Dados

### Pré-requisitos

- PostgreSQL 14 ou superior instalado e rodando
- Usuário `postgres` com senha `postgres` (ou ajuste o `application.properties`)

### Criação do banco

Conecte ao PostgreSQL e execute:

```sql
CREATE DATABASE viagens_db;
```

O Flyway cria as tabelas automaticamente ao subir a aplicação via as migrations em `src/main/resources/db/migration/`.

### Migrations aplicadas automaticamente

| Arquivo | O que faz |
|---|---|
| `V1__criar_tabelas_iniciais.sql` | Cria as tabelas destino, atividade, usuario e usuario_perfis |
| `V2__dados_iniciais.sql` | Insere 2 usuários de teste e 3 destinos de exemplo |

---

## 5. Como Executar

### Pré-requisitos

- Java 8 ou superior (JDK)
- Maven 3.8+
- PostgreSQL rodando com o banco `viagens_db` criado

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/<seu-usuario>/viagens-api.git
cd viagens-api

# 2. (Opcional) Ajuste as credenciais do banco em:
# src/main/resources/application.properties

# 3. Execute
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

## 6. Usuários e Perfis de Teste

| Usuário | E-mail | Senha | Perfil | Permissões |
|---|---|---|---|---|
| Administrador | `admin@agencia.com` | `admin123` | ADMIN | Todos os endpoints |
| Usuário Padrão | `user@agencia.com` | `user123` | USER | GET (público) + PATCH /avaliacao |

### Como usar no Postman

1. Abra a requisição desejada
2. Vá na aba **Authorization**
3. Selecione **Basic Auth**
4. Preencha Username e Password conforme a tabela acima

---

## 7. Endpoints e Regras de Acesso

**Base URL:** `http://localhost:8080`

| Método | Rota | Descrição | Auth | Perfil |
|---|---|---|---|---|
| GET | `/destinos` | Lista todos os destinos | Não | Público |
| GET | `/destinos?busca={termo}` | Pesquisa por nome/localização | Não | Público |
| GET | `/destinos/{id}` | Detalha um destino | Não | Público |
| POST | `/destinos` | Cadastra novo destino | Sim | ADMIN |
| PUT | `/destinos/{id}` | Atualiza destino | Sim | ADMIN |
| PATCH | `/destinos/{id}/avaliacao` | Registra avaliação | Sim | USER/ADMIN |
| DELETE | `/destinos/{id}` | Remove destino | Sim | ADMIN |

---

## 8. Exemplos de Requisições

### Listar destinos (público)
```bash
curl http://localhost:8080/destinos
```

### Cadastrar destino (ADMIN)
```bash
curl -X POST http://localhost:8080/destinos \
  -H "Content-Type: application/json" \
  -u admin@agencia.com:admin123 \
  -d '{
    "nome": "Lisboa",
    "localizacao": "Lisboa, Portugal",
    "descricao": "Capital histórica de Portugal.",
    "atividades": ["Torre de Belém", "Alfama"]
  }'
```

### Avaliar destino (USER ou ADMIN)
```bash
curl -X PATCH http://localhost:8080/destinos/1/avaliacao \
  -H "Content-Type: application/json" \
  -u user@agencia.com:user123 \
  -d '{ "nota": 4.8 }'
```

### Tentar cadastrar sem autenticação (deve retornar 401)
```bash
curl -X POST http://localhost:8080/destinos \
  -H "Content-Type: application/json" \
  -d '{ "nome": "Teste" }'
```

### Tentar cadastrar com perfil USER (deve retornar 403)
```bash
curl -X POST http://localhost:8080/destinos \
  -H "Content-Type: application/json" \
  -u user@agencia.com:user123 \
  -d '{ "nome": "Teste", "localizacao": "Teste" }'
```

---

### Formato de erro de autenticação

```json
{
  "timestamp": "2026-09-19T10:00:00",
  "status": 401,
  "erro": "Unauthorized",
  "mensagem": "Credenciais inválidas"
}
```

### Formato de erro de autorização

```json
{
  "timestamp": "2026-09-19T10:00:00",
  "status": 403,
  "erro": "Forbidden",
  "mensagem": "Acesso negado"
}
```

---

*Desenvolvido para o Desafio 2 de Desenvolvimento de Sistemas Web — UniSENAI ADS, 2026.*
