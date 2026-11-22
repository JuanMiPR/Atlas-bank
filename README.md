# Atlas Bank

Atlas Bank is a small but complete core-banking sample built with **Spring Boot 4.1.2** and **Java 25**, written with **Hexagonal Architecture (Ports & Adapters)** and **Domain-Driven Design** in mind.

The project implements account management, money transfers with a state machine, per-account-type fee strategies, a pluggable validator pipeline, a stubbed fraud check, an account dashboard, and an AI chat assistant that can operate on accounts through tool calling.

Architectural rules are not just documented — they are enforced by **ArchUnit** tests that run as part of the build.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Authentication](#authentication)
- [REST API](#rest-api)
- [Error Handling](#error-handling)
- [Domain Model](#domain-model)
- [AI Assistant](#ai-assistant)
- [Testing](#testing)
- [Project Structure](#project-structure)

---

## Features

- **Account management**: create, list, fetch, and close accounts (`SAVINGS`, `CHECKING`, `PREMIUM`), each with `EUR`/`USD` balance, unique account number, and `ACTIVE`/`FROZEN`/`CLOSED` lifecycle.
- **Money transfers**: atomic debit/credit with domain validation before any state change.
- **Validator pipeline**: ordered validators (account status → sufficient funds → fraud) injected as a `List<TransferValidator>`.
- **Fee strategies**: `CheckingFeeCalculator` (1.5%), `SavingsFeeCalculator` (1.0%), `PremiumFeeCalculator` (0%), with `DefaultFeeCalculator` as fallback. The first matching calculator wins.
- **Transaction state machine**: `Pending → Validated → Executed`, with `Rejected` and `Reversed` branches, implemented as explicit state objects rather than an enum switch.
- **Fraud checking**: `FraudCheckPort` with a local stub adapter that blocks transfers above `1,000,000` and allows everything else.
- **Account dashboard**: aggregated read model with account data plus its transactions.
- **Domain events**: `TransactionExecutedEvent` and `AccountClosedEvent`, published when transactions are persisted and consumed by `AuditListener` and `NotificationListener`.
- **AI assistant**: Spring AI chat endpoint wired to a local Ollama model, with `@Tool` methods to check balances, transfer money, and close accounts.
- **Security**: OAuth2 resource server (JWT) with role-based authorization.
- **Contract-first errors**: RFC 7807 `ProblemDetail` responses for every failure case.

---

## Architecture

```
        HTTP / AI requests
                │
    ┌───────────▼────────────┐
    │  adapter.in (REST, AI) │  controllers, DTOs, mapping
    └───────────┬────────────┘
                │  port.in (use cases)
    ┌───────────▼────────────┐
    │      application       │  services, commands, queries, orchestration
    └───────────┬────────────┘
                │  port.out (repositories, fraud check)
    ┌───────────▼────────────┐
    │      infrastructure    │  JPA, fraud stub, listeners, config
    └───────────┬────────────┘
                │
    ┌───────────▼────────────┐
    │        domain          │  entities, value objects, states, fees, validators
    └────────────────────────┘
```

Key rules (verified by ArchUnit on every `./mvnw test`):

| Rule | Test |
|---|---|
| `domain/` has no dependency on Spring, application, or infrastructure | `HexagonalArchitectureTest` |
| `application/` does not depend on infrastructure or Spring Security | `HexagonalArchitectureTest` |
| `*Controller` classes only live in `adapter.in.rest` | `NamingConventionTest` |
| `*UseCase` interfaces only live in `port.in` | `NamingConventionTest` |
| `port.in` / `port.out` contain interfaces only | `NamingConventionTest` |
| No package cycles | `NoCyclicDependenciesTest` |
| Security types stay in infrastructure | `SecurityIsolationTest` |

Design notes:

- Domain collaborators (`TransferDomainService`, validators, fee calculators) carry **no Spring annotations**. They are registered as beans in `DomainBeanConfig`, which keeps the domain framework-free.
- `AuditableAccountService` is `@Primary` and delegates to `AccountServiceImpl` (injected via `@Qualifier`), adding cross-cutting behavior without polluting the use-case implementation. Always inject the use-case interfaces, never the implementation.
- MapStruct mappers (`AccountMapper`, `TransactionMapper`) use `componentModel = "spring"`; generated sources land in `target/generated-sources`. Annotation processor order is Lombok → MapStruct → `lombok-mapstruct-binding` and must be preserved.

---

## Requirements

| Tool | Version / Notes |
|---|---|
| **JDK** | **25** (the build targets `java.version=25`; Java 21 will not compile) |
| **Maven** | Not required — the project ships the `./mvnw` wrapper (Maven 3.10.0, script-only wrapper, no wrapper JAR) |
| **Docker / Docker Compose** | Optional. Only needed for Keycloak when you want authenticated HTTP calls |
| **Ollama** | Optional. Only needed for the AI chat endpoint |
| **Spring Boot** | 4.1.2 (managed by the parent POM) |

Runtime dependencies: Spring Web MVC, Spring Data JPA, Validation, OAuth2 Resource Server, Cache, H2, Spring AI (OpenAI starter), Lombok, MapStruct 1.6.3, ArchUnit 1.5.1 (test).

Verify your JDK:

```bash
java -version   # must report 25.x
```

---

## Getting Started

Clone the repository:

```bash
git clone https://github.com/JuanMiPR/Atlas-bank.git
cd Atlas-bank
```

Run the full test suite (compile + unit + ArchUnit):

```bash
./mvnw -q test
```

Run a single test class:

```bash
./mvnw test -Dtest=MoneyDomainTest
```

Start the application on port **8080**:

```bash
./mvnw spring-boot:run
```

The database is **in-memory H2 with `ddl-auto: create-drop`**, so all data is reset on every restart and no migrations are needed.

H2 console: <http://localhost:8080/h2-console>

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:atlasbank` |
| User | `sa` |
| Password | *(empty)* |

Optional — start Keycloak (only for authenticated calls):

```bash
docker compose -f docker/docker-compose.yaml up -d
```

This exposes Keycloak on `:8181` backed by PostgreSQL on `:5433`. The compose file has **no realm import and no volume**, so a fresh instance requires you to create the `atlas-bank` realm, client, and roles manually.

> Tests and application startup do **not** contact Keycloak: JWT issuer metadata is fetched lazily.

---

## Configuration

Main configuration lives in `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:atlasbank
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8181/realms/atlas-bank
  ai:
    openai:
      api-key: ollama
      base-url: http://localhost:11434/v1
      chat:
        model: qwen3:8b
```

Override any property with standard Spring Boot mechanisms, e.g.:

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=9090 --spring.jpa.show-sql=false"
```

`spring.jpa.show-sql` is enabled by default, so prefer `./mvnw -q` to keep build/test output readable.

---

## Authentication

The app is an **OAuth2 resource server**: requests are authenticated with a JWT from Keycloak.

| Requirement | Value |
|---|---|
| Issuer | `http://localhost:8181/realms/atlas-bank` |
| Realm | `atlas-bank` |
| Roles | `ADMIN`, `USER` (read from `realm_access.roles`) |

Example authenticated call:

```bash
TOKEN=$(curl -s -X POST http://localhost:8181/realms/atlas-bank/protocol/openid-connect/token \
  -d grant_type=password \
  -d client_id=atlas-client \
  -d username=admin \
  -d password=admin | jq -r .access_token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/accounts
```

> **Auth gotcha:** `SecurityConfig` uses `hasRole("ADMIN")` / `hasRole("USER")`, which Spring Security evaluates against authorities prefixed with `ROLE_`. The custom JWT converter copies `realm_access.roles` verbatim without adding that prefix. Tokens therefore need authorities literally named `ROLE_ADMIN` / `ROLE_USER` (i.e. define the Keycloak realm roles as `ROLE_ADMIN` and `ROLE_USER`), or the converter / checks must be aligned.

The H2 console and `/api/v1/ai/**` are permitted without authentication.

---

## REST API

Base path: `/api/v1`

### Accounts

| Method | Path | Description | Required role |
|---|---|---|---|
| `POST` | `/accounts` | Create an account | `ADMIN` |
| `GET` | `/accounts` | List all accounts | `ADMIN` |
| `GET` | `/accounts/{id}` | Get one account | `ADMIN`, `USER` |
| `GET` | `/accounts/{id}/dashboard` | Account + transactions read model | `ADMIN`, `USER` |
| `PATCH` | `/accounts/{id}/close` | Close an account | authenticated |

Create account:

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ES9121000418450200051332",
    "ownerName": "Jane Doe",
    "email": "jane.doe@example.com",
    "type": "SAVINGS",
    "balance": 2500.00
  }'
```

Validation rules: `accountNumber`, `ownerName`, and `email` are required (email must be well-formed), `type` is required, and `balance` must be zero or positive. Account numbers must be unique.

### Transactions

| Method | Path | Description | Required role |
|---|---|---|---|
| `POST` | `/transactions/transfer` | Transfer money between two accounts | `ADMIN`, `USER` |
| `GET` | `/transactions/{id}/transactions` | List transactions of an account | `ADMIN`, `USER` |

Transfer:

```bash
curl -X POST http://localhost:8080/api/v1/transactions/transfer \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId": 1, "toAccountId": 2, "amount": 150.00}'
```

`fromAccountId` and `toAccountId` must be different (custom `@DifferentAccounts` constraint), and `amount` must be positive.

### AI

| Method | Path | Description | Required role |
|---|---|---|---|
| `POST` | `/ai/chat` | Chat with the banking assistant (plain text body) | public |

---

## Error Handling

All errors are returned as RFC 7807 `ProblemDetail` objects by `GlobalExceptionHandler`:

| Exception | Status |
|---|---|
| `AccountNotFoundException` | `404 Not Found` |
| `InsufficientFoundException` | `422 Unprocessable Content` |
| `AccountNotActiveException` | `422 Unprocessable Content` |
| `FraudCheckException` | `422 Unprocessable Content` |
| `MethodArgumentNotValidException` (bean validation) | `400 Bad Request` |
| Any other exception | `500 Internal Server Error` |

Example:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Account not found with id: 42"
}
```

---

## Domain Model

**Value objects** (`domain/model/shared`): `Money` (amount + `CurrencyEnum` of `EUR`/`USD`), `Email` (validated), `FraudCheckResult`.

**Account** (`domain/model/account`): `id`, `accountNumber`, `ownerName`, `email`, `type`, `balance` (`Money`), `status`, `createdAt`, `customerId`. Behavior is encapsulated in `deposit`, `withdraw`, and `close`; invariants are guarded by domain exceptions.

**Customer** (`domain/model/customer`): domain + persistence only (`CustomerRepositoryPort`, `JpaCustomerRepositoryAdapter`). There is **no customer use case, service, or REST endpoint yet**.

**Transaction** (`domain/model/transaction`): amount, currency, fee, source/target account, type (`DEPOSIT`/`WITHDRAW`/`TRANSFER`), status.

State machine:

```
Pending ──► Validated ──► Executed
   │            │
   └────────────┴──────► Rejected

Executed ──► Reversed
```

Each state is a class implementing `TransactionState`, so allowed transitions stay inside the domain.

**Fees** are selected by account type (first matching calculator wins):

| Account type | Fee |
|---|---|
| `CHECKING` | 1.5% of the amount |
| `SAVINGS` | 1.0% of the amount |
| `PREMIUM` | 0% |
| fallback | 0% (`DefaultFeeCalculator`) |

**Persistence duplication is intentional**: `infrastructure.adapter.out.persistence.Money` and `Email` are JPA `@Embeddable` twins of the domain value objects, keeping the domain free of JPA annotations. `AccountPersistenceMapper` disambiguates them with fully-qualified names. Transactions, by contrast, persist the domain `Money` directly — do not assume a single shared embeddable.

`JpaTransactionRepositoryAdapter.save` publishes the transaction's domain events and then clears them; add new events to `domainEvents`. `AuditListener` and `NotificationListener` currently just log.

---

## AI Assistant

`/api/v1/ai/chat` exposes a `ChatClient` backed by **Ollama** through the Spring AI OpenAI-compatible API:

| Setting | Value |
|---|---|
| Base URL | `http://localhost:11434/v1` |
| Model | `qwen3:8b` |
| API key | `ollama` (placeholder, not validated by Ollama) |

Start the model locally:

```bash
ollama serve
ollama pull qwen3:8b
```

Then chat:

```bash
curl -X POST http://localhost:8080/api/v1/ai/chat \
  -H "Content-Type: text/plain" \
  -d "What is the balance of account 1?"
```

`AtlasBankTools` exposes three `@Tool` methods to the model: `getAccountBalance`, `transferMoney`, and `closeAccount`. Because tools mutate real state and the endpoint is public, this adapter is a demo surface — do not expose it in production without authorization and limits.

---

## Testing

```bash
./mvnw -q test
```

Test suites currently include:

| Suite | Coverage |
|---|---|
| `archtest/*` | Hexagonal boundaries, naming conventions, no cycles, security isolation |
| `MoneyDomainTest` | `Money` arithmetic and invariants |
| `EmailDomainTest` | Email validation |
| `AccountDomainTest` | Deposits, withdrawals, closing, status rules |
| `AtlasBankApplicationTests` | Spring context loads |

There is no CI, linter, or formatter configured in the repository: **compilation + tests are the quality gate**.

---

## Project Structure

```
atlas-bank/
├── docker/docker-compose.yaml     # Keycloak + PostgreSQL for local auth
├── mvnw, mvnw.cmd, .mvn/          # Maven wrapper
├── pom.xml
├── AGENTS.md                      # Notes for AI coding agents
└── src/
    ├── main/
    │   ├── java/com/atlas/bank/atlas_bank/
    │   │   ├── AtlasBankApplication.java
    │   │   ├── domain/                    # framework-free core
    │   │   │   ├── model/                 # account, customer, transaction, shared
    │   │   │   ├── event/                 # TransactionExecutedEvent, AccountClosedEvent
    │   │   │   ├── exception/
    │   │   │   ├── service/               # TransferDomainService
    │   │   │   ├── strategy/fee/          # fee calculators
    │   │   │   └── validation/            # transfer validators
    │   │   ├── application/
    │   │   │   ├── command/               # Create/Transfer/Close commands
    │   │   │   ├── query/                 # read models (dashboard, statement)
    │   │   │   ├── port/in/               # use cases
    │   │   │   ├── port/out/              # repositories, fraud check
    │   │   │   ├── service/               # use-case implementations
    │   │   │   └── validation/            # FraudValidator
    │   │   └── infrastructure/
    │   │       ├── adapter/in/rest/       # controllers, DTOs, mappers
    │   │       ├── adapter/in/ai/         # chat controller + tools
    │   │       ├── adapter/out/persistence/
    │   │       ├── adapter/out/fraud/
    │   │       ├── adapter/listener/      # audit, notification
    │   │       └── config/                # Security, domain beans, AI, error handler
    │   └── resources/application.yaml
    └── test/java/com/atlas/bank/atlas_bank/
        ├── archtest/
        └── domain/model/
```

Package root is `com.atlas.bank.atlas_bank` (underscore, unlike the artifact's hyphen).
