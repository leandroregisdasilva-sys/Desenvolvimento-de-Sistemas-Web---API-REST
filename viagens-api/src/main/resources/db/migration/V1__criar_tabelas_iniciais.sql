-- ─────────────────────────────────────────────────────────────────────────────
-- V1: Criação das tabelas iniciais da API de Viagens
-- ─────────────────────────────────────────────────────────────────────────────

-- Tabela de destinos turísticos
CREATE TABLE destino (
    id                BIGSERIAL PRIMARY KEY,
    nome              VARCHAR(100) NOT NULL,
    localizacao       VARCHAR(150) NOT NULL,
    descricao         VARCHAR(1000),
    media_avaliacoes  DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_avaliacoes  INTEGER NOT NULL DEFAULT 0
);

-- Tabela de atividades (relacionamento 1:N com destino)
CREATE TABLE atividade (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(200) NOT NULL,
    destino_id BIGINT NOT NULL,
    CONSTRAINT fk_atividade_destino FOREIGN KEY (destino_id) REFERENCES destino(id) ON DELETE CASCADE
);

-- Tabela de usuários do sistema
CREATE TABLE usuario (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Tabela de perfis (roles) dos usuários
CREATE TABLE usuario_perfis (
    usuario_id BIGINT NOT NULL,
    perfil     VARCHAR(50) NOT NULL,
    CONSTRAINT fk_usuario_perfil FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);
