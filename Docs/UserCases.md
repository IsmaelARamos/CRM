#### Use Cases — Issue #1: Persistência e Migrations

#### UC-01: Inicialização Automatizada do Esquema de Banco de Dados

• Ator Principal / Primary Actor: Sistema (Spring Boot + Flyway) / System.
• Pré-condições / Preconditions: Container do PostgreSQL ativo no Docker e credenciais válidas no application.yml.
• Fluxo Principal / Main Flow:
1. PT: A aplicação Java inicia e estabelece conexão com o banco crm_enterprise_db via HikariCP.
EN: The Java application starts and establishes a connection to the crm_enterprise_db database via HikariCP.
2. PT: O Flyway verifica a existência da tabela de controle flyway_schema_history.
EN: Flyway checks for the existence of the flyway_schema_history control table.
3. PT: O Flyway identifica a migration pendente V1__create_table_users.sql, executa o script SQL criando a tabela tb_users e insere o usuário administrador inicial.
EN: Flyway identifies the pending V1__create_table_users.sql migration, executes the SQL script creating the tb_users table, and inserts the initial admin user.
4. PT: O Hibernate valida se as entidades mapeadas correspondem às tabelas criadas e libera a inicialização do servidor web.
EN: Hibernate validates that mapped entities match the created tables and allows the web server to start.
• Fluxo de Exceção / Exception Flow:
• PT: Se o banco estiver inacessível ou houver divergência no checksum da migration, o sistema aborta a inicialização imediatamente para evitar corrupção de dados.
• EN: If the database is unreachable or there is a migration checksum mismatch, the system aborts startup immediately to prevent data corruption.


#### Use Cases — Issue #2: Segurança e Autenticação JWT


#### UC-02: Autenticação de Usuário e Emissão de Token (Login)

• Ator Principal / Primary Actor: Usuário do CRM (Vendedor ou Administrador) / CRM User.
• Pré-condições / Preconditions: Usuário previamente cadastrado na tabela tb_users com senha criptografada.
• Fluxo Principal / Main Flow:
1. PT: O cliente envia uma requisição POST /api/v1/auth/login contendo e-mail e senha no corpo JSON.
EN: The client sends a POST /api/v1/auth/login request containing email and password in the JSON body.
2. PT: O AuthenticationManager busca o usuário no PostgreSQL via UserDetailsService e compara o hash da senha utilizando BCrypt.
EN: The AuthenticationManager fetches the user from PostgreSQL via UserDetailsService and compares the password hash using BCrypt.
3. PT: Com as credenciais válidas, o JwtService gera um Token JWT assinado com chave HMAC-SHA256 e prazo de expiração de 24 horas.
EN: With valid credentials, JwtService generates a JWT Token signed with an HMAC-SHA256 key and a 24-hour expiration time.
4. PT: A API retorna HTTP 200 OK entregando o token gerado ao cliente.
EN: The API returns HTTP 200 OK delivering the generated token to the client.
• Fluxo de Exceção / Exception Flow:
• PT: Se o e-mail não existir ou a senha estiver incorreta, o sistema bloqueia o acesso e retorna HTTP 401 Unauthorized (ou 403 Forbidden) sem expor qual dado falhou.
• EN: If the email does not exist or the password is incorrect, the system blocks access and returns HTTP 401 Unauthorized (or 403 Forbidden) without exposing which field failed.



#### UC-03: Interceptação e Validação de Requisições Protegidas

• Ator Principal / Primary Actor: Cliente Autenticado (Front-end / Postman / Swagger) / Authenticated Client.
• Pré-condições / Preconditions: O cliente possui um Token JWT válido gerado no UC-02.
• Fluxo Principal / Main Flow:
1. PT: O cliente realiza uma requisição para uma rota protegida (ex: GET /api/v1/leads) enviando o cabeçalho Authorization: Bearer <token>.
EN: The client makes a request to a protected route (e.g., GET /api/v1/leads) sending the Authorization: Bearer <token> header.
2. PT: O JwtAuthenticationFilter intercepta a chamada antes que ela chegue ao Controller e extrai o token.
EN: The JwtAuthenticationFilter intercepts the call before it reaches the Controller and extracts the token.
3. PT: O JwtService valida a assinatura criptográfica, verifica se o token não expirou e extrai o e-mail e os perfis (ROLE_ADMIN / ROLE_USER).
EN: JwtService validates the cryptographic signature, checks that the token has not expired, and extracts the email and roles (ROLE_ADMIN / ROLE_USER).
4. PT: O filtro registra o usuário no SecurityContextHolder do Spring e libera a execução para o Controller responder a requisição.
EN: The filter registers the user in Spring's SecurityContextHolder and allows execution to proceed to the Controller.
• Fluxo de Exceção / Exception Flow:
• PT: Se o cabeçalho estiver ausente, mal formatado, com assinatura adulterada ou expirado, o filtro interrompe a requisição e retorna HTTP 401/403.
• EN: If the header is missing, malformed, has a tampered signature, or is expired, the filter halts the request and returns HTTP 401/403.

#### UC-04: Cadastro de Novo Usuário (Registro)
* **Ator Principal / Primary Actor:** Administrador ou Novo Usuário do CRM / CRM Admin or New User.
* **Pré-condições / Preconditions:** E-mail informado não deve existir previamente na tabela `tb_users`.
* **Fluxo Principal / Main Flow:**
    1. **PT:** O cliente envia uma requisição `POST /api/v1/auth/register` com nome, e-mail, senha e perfil (`ROLE_USER` ou `ROLE_ADMIN`).
       **EN:** The client sends a `POST /api/v1/auth/register` request with name, email, password, and role (`ROLE_USER` or `ROLE_ADMIN`).
    2. **PT:** O `AuthenticationService` criptografa a senha em texto plano utilizando `BCryptPasswordEncoder`.
       **EN:** `AuthenticationService` encrypts the plain-text password using `BCryptPasswordEncoder`.
    3. **PT:** O sistema persiste o novo registro no PostgreSQL via `UserRepository` e gera imediatamente um Token JWT válido.
       **EN:** The system persists the new record in PostgreSQL via `UserRepository` and immediately generates a valid JWT Token.
    4. **PT:** A API retorna HTTP `200 OK` (ou `201 Created`) entregando o token de acesso.
       **EN:** The API returns HTTP `200 OK` (or `201 Created`) delivering the access token.
* **Fluxo de Exceção / Exception Flow:**
    * **PT:** Se o e-mail já estiver cadastrado no banco, a restrição `UNIQUE` barra a inserção e o sistema retorna erro de conflito/requisição inválida.
    * **EN:** If the email is already registered in the database, the `UNIQUE` constraint blocks insertion and the system returns a conflict/bad request error.