-- ─────────────────────────────────────────────────────────────────────────────
-- V2: Dados iniciais — usuários e destinos de exemplo
-- Senhas geradas com BCrypt (custo 10):
--   admin123 -> $2a$10$...
--   user123  -> $2a$10$...
-- ─────────────────────────────────────────────────────────────────────────────

-- Usuário ADMIN
INSERT INTO usuario (nome, email, senha) VALUES
    ('Administrador', 'admin@agencia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO usuario_perfis (usuario_id, perfil)
    SELECT id, 'ROLE_ADMIN' FROM usuario WHERE email = 'admin@agencia.com';

-- Usuário comum
INSERT INTO usuario (nome, email, senha) VALUES
    ('Usuário Padrão', 'user@agencia.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1GkqUKaWm');

INSERT INTO usuario_perfis (usuario_id, perfil)
    SELECT id, 'ROLE_USER' FROM usuario WHERE email = 'user@agencia.com';

-- Destinos de exemplo
INSERT INTO destino (nome, localizacao, descricao, media_avaliacoes, total_avaliacoes) VALUES
    ('Paris', 'Paris, França', 'A Cidade Luz encanta com sua arquitetura, gastronomia e arte.', 4.75, 2),
    ('Rio de Janeiro', 'Rio de Janeiro, Brasil', 'Praias deslumbrantes, samba e o famoso Cristo Redentor.', 4.80, 1),
    ('Kyoto', 'Kyoto, Japão', 'A cidade dos templos, geishas e jardins zen.', 4.90, 1);

-- Atividades dos destinos
INSERT INTO atividade (nome, destino_id)
    SELECT 'Torre Eiffel', id FROM destino WHERE nome = 'Paris';
INSERT INTO atividade (nome, destino_id)
    SELECT 'Museu do Louvre', id FROM destino WHERE nome = 'Paris';
INSERT INTO atividade (nome, destino_id)
    SELECT 'Champs-Élysées', id FROM destino WHERE nome = 'Paris';

INSERT INTO atividade (nome, destino_id)
    SELECT 'Cristo Redentor', id FROM destino WHERE nome = 'Rio de Janeiro';
INSERT INTO atividade (nome, destino_id)
    SELECT 'Pão de Açúcar', id FROM destino WHERE nome = 'Rio de Janeiro';

INSERT INTO atividade (nome, destino_id)
    SELECT 'Templo Kinkaku-ji', id FROM destino WHERE nome = 'Kyoto';
INSERT INTO atividade (nome, destino_id)
    SELECT 'Arashiyama', id FROM destino WHERE nome = 'Kyoto';
