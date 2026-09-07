-- Seed exclusivo de %dev (sql-load-script do Hibernate). Categorias, severidades, status e
-- estados agora vêm da migration V1.0.1__reference_data.sql, aplicada em todo ambiente.

-- Insert User values
INSERT INTO vozdaruauser (id, email, password, phone, role) VALUES (1, 'pedro-hos@outlook.com', '$2a$10$Uc.SZ0hvGJQlYdsAp7be1.lFjmOnc7aAr4L0YY3/VN3oK.F8zJHRG', '12996448715', 'ADMIN');

-- Reset sequence for Status
ALTER SEQUENCE vozdaruauser_seq RESTART WITH 2;
