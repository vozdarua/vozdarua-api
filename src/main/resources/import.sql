-- Insert categories from vozdarua_categorias.csv
INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (1, 'Buraco no asfalto', '🕳️', 'Cratera na rua, remendo solto, asfalto afundado', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (1, 'buraco'), (1, 'asfalto'), (1, 'cova'), (1, 'cratera'), (1, 'remendo'), (1, 'via'), (1, 'pista');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (2, 'Calçada danificada', '🚶', 'Piso quebrado, desnível, falta de rampa de acessibilidade', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (2, 'calçada'), (2, 'passeio'), (2, 'piso'), (2, 'meio-fio'), (2, 'acessibilidade'), (2, 'rampa');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (3, 'Iluminação pública', '💡', 'Poste apagado, lâmpada queimada, rua escura à noite', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (3, 'luz'), (3, 'poste'), (3, 'lâmpada'), (3, 'iluminação'), (3, 'escuro'), (3, 'apagado');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (4, 'Sinalização e trânsito', '🚦', 'Semáforo quebrado, placa faltando, faixa apagada, lombada sem placa', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (4, 'semáforo'), (4, 'placa'), (4, 'faixa'), (4, 'sinalização'), (4, 'trânsito'), (4, 'lombada'), (4, 'pare');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (5, 'Água e esgoto', '💧', 'Vazamento, esgoto a céu aberto, falta d''água, bueiro entupido', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (5, 'água'), (5, 'esgoto'), (5, 'vazamento'), (5, 'bueiro'), (5, 'fossa'), (5, 'encanamento'), (5, 'hidrante'), (5, 'cano');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (6, 'Alagamento e enchente', '🌊', 'Rua alagada na chuva, valão transbordando, sarjeta entupida', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (6, 'alagamento'), (6, 'enchente'), (6, 'inundação'), (6, 'chuva'), (6, 'dreno'), (6, 'sarjeta'), (6, 'valão'), (6, 'água');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (7, 'Risco de deslizamento', '⛰️', 'Encosta instável, erosão, talude sem contenção, morro com rachaduras', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (7, 'deslizamento'), (7, 'encosta'), (7, 'morro'), (7, 'talude'), (7, 'erosão'), (7, 'desmoronamento'), (7, 'chuva'), (7, 'risco'), (7, 'terra');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (8, 'Árvore de risco', '🌳', 'Árvore inclinada, galho sobre fio elétrico, raiz levantando calçada', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (8, 'árvore'), (8, 'galho'), (8, 'fio'), (8, 'raiz'), (8, 'inclinada'), (8, 'queda'), (8, 'poda'), (8, 'vegetação'), (8, 'risco');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (9, 'Ilha de calor e sombra', '🌡️', 'Ausência de árvores, praça sem sombreamento, asfalto excessivo', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (9, 'calor'), (9, 'sombra'), (9, 'árvore'), (9, 'temperatura'), (9, 'ilha de calor'), (9, 'sombreamento'), (9, 'vegetação');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (10, 'Queimada e poluição do ar', '🔥', 'Fogo em terreno baldio, queima de lixo, fumaça em área urbana', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (10, 'queimada'), (10, 'fogo'), (10, 'fumaça'), (10, 'poluição'), (10, 'ar'), (10, 'incêndio'), (10, 'terreno');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (11, 'Área insegura', '🚨', 'Ponto de tráfico, beco sem iluminação, local de crime frequente', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (11, 'inseguro'), (11, 'tráfico'), (11, 'crime'), (11, 'perigo'), (11, 'beco'), (11, 'risco'), (11, 'violência'), (11, 'assalto'), (11, 'área');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (12, 'Infraestrutura de segurança', '🔦', 'Câmera quebrada, guarita abandonada, cerca danificada', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (12, 'câmera'), (12, 'segurança'), (12, 'guarita'), (12, 'cerca'), (12, 'vigilância'), (12, 'cftv'), (12, 'monitoramento');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (13, 'Rua sem iluminação', '⚫', 'Trecho totalmente sem luz, poste inexistente, área perigosa à noite', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (13, 'escuro'), (13, 'sem luz'), (13, 'poste'), (13, 'noite'), (13, 'perigoso'), (13, 'iluminação'), (13, 'rua');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (14, 'Lixo e entulho', '🗑️', 'Entulho abandonado, lixo acumulado, descarte irregular', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (14, 'lixo'), (14, 'entulho'), (14, 'lixão'), (14, 'descarte'), (14, 'sujeira'), (14, 'resíduo'), (14, 'coleta');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (15, 'Poluição e meio ambiente', '🌿', 'Descarte em rio, córrego poluído, desmatamento em área verde', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (15, 'meio ambiente'), (15, 'rio'), (15, 'córrego'), (15, 'poluição'), (15, 'queimada'), (15, 'desmatamento'), (15, 'esgoto');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (16, 'Foco de dengue / mosquito', '🦟', 'Água parada, pneu, entulho, vasilhame, calha entupida com água', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (16, 'dengue'), (16, 'mosquito'), (16, 'aedes'), (16, 'água parada'), (16, 'larva'), (16, 'foco'), (16, 'pneu'), (16, 'zika'), (16, 'chikungunya');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (17, 'Saúde pública', '🏥', 'UBS fechada, falta de médico, vacina em falta, longa espera', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (17, 'saúde'), (17, 'ubs'), (17, 'posto'), (17, 'médico'), (17, 'vacina'), (17, 'fila'), (17, 'atendimento'), (17, 'doença');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (18, 'Escola e educação', '🏫', 'Escola sem professor, infiltração, falta de merenda, estrutura ruim', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (18, 'escola'), (18, 'creche'), (18, 'professor'), (18, 'merenda'), (18, 'educação'), (18, 'ensino'), (18, 'aluno');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (19, 'Transporte público', '🚌', 'Ônibus não passa, ponto sem cobertura, horário errado', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (19, 'ônibus'), (19, 'transporte'), (19, 'ponto'), (19, 'van'), (19, 'parada'), (19, 'itinerário'), (19, 'linha'), (19, 'bus');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (20, 'Praça e parque', '🏞️', 'Brinquedo quebrado, banco danificado, iluminação ruim, mato alto', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (20, 'praça'), (20, 'parque'), (20, 'jardim'), (20, 'brinquedo'), (20, 'quadra'), (20, 'lazer'), (20, 'banco');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (21, 'Espaço público degradado', '🏚️', 'Praça abandonada, área pública sem manutenção, terreno baldio', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (21, 'praça'), (21, 'abandonada'), (21, 'degradado'), (21, 'construção'), (21, 'terreno'), (21, 'baldio'), (21, 'manutenção'), (21, 'área pública');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (22, 'Ocupação irregular', '⚠️', 'Construção irregular, comércio na calçada, barraco em área pública', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (22, 'irregular'), (22, 'invasão'), (22, 'construção'), (22, 'barraco'), (22, 'camelô'), (22, 'ambulante');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (23, 'Animais e zoonoses', '🐕', 'Cães soltos, animais abandonados, foco de escorpião, pombos', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (23, 'animal'), (23, 'cachorro'), (23, 'gato'), (23, 'escorpião'), (23, 'pombo'), (23, 'abandono'), (23, 'zoonose'), (23, 'rato');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (24, 'Barulho e perturbação', '🔊', 'Som alto em horário proibido, obra noturna, bar perturbando', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (24, 'barulho'), (24, 'som'), (24, 'ruído'), (24, 'perturbação'), (24, 'obra'), (24, 'vizinho'), (24, 'festa'), (24, 'poluição sonora');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (25, 'Acessibilidade', '♿', 'Sem rampa, calçada obstruída, banheiro inacessível, elevador quebrado', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (25, 'acessibilidade'), (25, 'rampa'), (25, 'deficiente'), (25, 'cadeira de rodas'), (25, 'pcd'), (25, 'elevador'), (25, 'calçada');

INSERT INTO Category (id, name, icon, description, createdAt, updatedAt) VALUES (26, 'Outro problema', '📋', 'Qualquer situação que não se encaixa nas categorias acima', '2026-06-12 00:00:00', '2026-06-12 00:00:00');
INSERT INTO category_tags (category_id, tag) VALUES (26, 'outro'), (26, 'outros'), (26, 'diferente'), (26, 'diverso');

-- Reset sequence to continue from next ID
ALTER SEQUENCE category_seq RESTART WITH 27;

-- Insert Severity levels
INSERT INTO Severity (id, name, icon, createdAt, updatedAt) VALUES (1, 'Baixo', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO Severity (id, name, icon, createdAt, updatedAt) VALUES (2, 'Médio', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO Severity (id, name, icon, createdAt, updatedAt) VALUES (3, 'Alto', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');

-- Reset sequence for Severity
ALTER SEQUENCE severity_seq RESTART WITH 4;

-- Insert Status values
INSERT INTO Status (id, name, icon, createdAt, updatedAt) VALUES (1, 'Aceito', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO Status (id, name, icon, createdAt, updatedAt) VALUES (2, 'Resolvido', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO Status (id, name, icon, createdAt, updatedAt) VALUES (3, 'Em análise', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO Status (id, name, icon, createdAt, updatedAt) VALUES (4, 'Aberto', '', '2026-06-16 00:00:00', '2026-06-16 00:00:00');

-- Reset sequence for Status
ALTER SEQUENCE status_seq RESTART WITH 5;

-- Insert States

INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (1, 'Rondônia', 'RO','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (2, 'Acre', 'AC','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (3, 'Amazonas', 'AM','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (4, 'Roraima', 'RR','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (5, 'Pará', 'PA','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (6, 'Amapá', 'AP','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (7, 'Tocantins', 'TO','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (8, 'Maranhão', 'MA','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (9, 'Piauí', 'PI','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (10, 'Ceará', 'CE','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (11, 'Rio Grande do Norte', 'RN','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (12, 'Paraíba', 'PB','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (13, 'Pernambuco', 'PE','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (14, 'Alagoas', 'AL','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (15, 'Sergipe', 'SE','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (16, 'Bahia', 'BA','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (17, 'Minas Gerais', 'MG','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (18, 'Espírito Santo', 'ES','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (19, 'Rio de Janeiro', 'RJ','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (20, 'São Paulo', 'SP','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (21, 'Paraná', 'PR','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (22, 'Santa Catarina', 'SC','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (23, 'Rio Grande do Sul', 'RS','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (24, 'Mato Grosso do Sul', 'MS','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (25, 'Mato Grosso', 'MT','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (26, 'Goiás', 'GO','2026-06-16 00:00:00', '2026-06-16 00:00:00');
INSERT INTO state (id, name, uf, createdAt, updatedAt) VALUES (27, 'Distrito Federal', 'DF','2026-06-16 00:00:00', '2026-06-16 00:00:00');

-- Reset sequence for Status
ALTER SEQUENCE status_seq RESTART WITH 28;