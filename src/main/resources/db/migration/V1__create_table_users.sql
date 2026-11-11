-- Criando a tabela de usuários do CRM (Vendedores/Administradores)
CREATE TABLE tb_users (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          role VARCHAR(50) NOT NULL,
                          created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Inserindo um usuário administrador de teste
INSERT INTO tb_users (name, email, password, role)
VALUES ('Admin CRM', 'admin@crm.com', '$2a$10$eCea..fake.hash..xyz', 'ROLE_ADMIN');
