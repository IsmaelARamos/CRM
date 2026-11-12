## Arquitetura do Sistema / System Architecture

![Arquitetura do CRM Enterprise](docs/architecture.jpg)


### Fluxo de Autenticação JWT / JWT Authentication Flow

![Fluxo de Autenticação JWT](docs/jwt-authentication-flow.jpg)

# 🏢 CRM Enterprise API

![Java](https://shields.io)
![Spring Boot](https://shields.io)
![PostgreSQL](https://shields.io)
![Docker](https://shields.io)

> **PT-BR:** Sistema de CRM corporativo desenvolvido com arquitetura distribuída, foco em segurança *stateless*, observabilidade e práticas ágeis (Scrum).
>
> **EN-US:** Enterprise CRM system built with a distributed architecture, focusing on stateless security, observability, and Agile practices (Scrum).

---

## 🏗️ Arquitetura do Sistema / System Architecture

O ecossistema foi projetado para evoluir de um núcleo transacional seguro para uma arquitetura orientada a eventos desacoplada.

![Arquitetura do CRM Enterprise](docs/architecture.png)

---


### 🚧 Sprint 1 - Issue #2: Segurança e Autenticação JWT (Em Progresso / In Progress)

#### ✔️ Concluído / Completed:
* **Modelagem de Acesso (RBAC):** Criação da entidade `User` implementando o contrato `UserDetails`, `UserRepository` e perfis de acesso (`ROLE_ADMIN`, `ROLE_USER`).
* **Motor Criptográfico:** Implementação do `JwtService` utilizando assinatura **HMAC-SHA256** para geração, extração de *claims* e validação de expiração de tokens.
* **Configuração de Provedores (`ApplicationConfig`):** Definição do `UserDetailsService` integrado ao PostgreSQL e criptografia de senhas com `BCryptPasswordEncoder`.
* **Filtro e Cadeia de Segurança (`JwtAuthenticationFilter` & `SecurityConfig`):** Configuração de política *Stateless*, bloqueio padrão de rotas privadas e liberação de endpoints públicos (`/api/v1/auth/**` e Swagger).

#### ⏳ Pendente para Conclusão / Remaining Tasks:
* [ ] **DTOs de Autenticação:** Criação dos objetos de transferência (`RegisterRequest`, `AuthenticationRequest`, `AuthenticationResponse`).
* [ ] **Camada de Serviço (`AuthenticationService`):** Lógica de registro de novos usuários com hash de senha e validação de login.
* [ ] **Controlador REST (`AuthController`):** Exposição dos endpoints `/api/v1/auth/register` e `/api/v1/auth/login`.
* [ ] **Validação End-to-End:** Teste de geração e uso do Token JWT via Postman/Insomnia e abertura de Pull Request (`Closes #2`).

![Fluxo de Autenticação JWT](docs/jwt-authentication-flow.png)



![Fluxo de Autenticação JWT](docs/jwt-authentication-flow.png)

---

## 🛠️ Como Executar Localmente / How to Run Locally

### Pré-requisitos / Prerequisites
* **Java 21** instalado / installed
* **Docker** e **Docker Compose** ativos / running
* **Maven**

### Passo a Passo / Step-by-Step

1. **Subir o banco de dados no Docker / Start the database container:**
   ```bash
   docker compose up -d
   ```

2. **Compilar e rodar a aplicação / Build and run the application:**
   ```bash
   mvn clean spring-boot:run
   ```
   *(O Flyway aplicará automaticamente as migrations na inicialização / Flyway will automatically apply migrations on startup).*

---

## 📋 Gestão Ágil / Agile Management (Scrum)

Este projeto simula um ciclo real de desenvolvimento utilizando **GitHub Projects** (Kanban), **User Stories** com critérios de aceitação, estimativas em **Story Points** e fluxo **Git Flow** com fechamento automatizado de *Issues* via *Pull Requests*.

📋 **[Ver Casos de Uso Detalhados / View Detailed Use Cases](docs/UserCases.md)**
