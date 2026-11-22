# Atlas Bank

A small Spring Boot service that models bank accounts, customers, and transfers using **Hexagonal Architecture** and **Domain-Driven Design** in a single deployable (modular monolith).

## Stack

- Java 25, Spring Boot 4.1
- Spring Web, Validation, Data JPA, Security (OAuth2 resource server)
- H2 in-memory database
- MapStruct + Lombok
- ArchUnit for architecture tests

## Architecture

The codebase has three layers, and each layer is organized **by bounded context**:

```
domain  ←  application  ←  infrastructure
```

Dependencies always point inward:

- `domain` has no Spring and no knowledge of persistence or HTTP.
- `application` orchestrates use cases through ports (`port.in` / `port.out`).
- `infrastructure` implements the outbound ports and exposes inbound adapters.

### Bounded contexts

| Context | Responsibility | Key types |
|---|---|---|
| `account` | Accounts, balances, account lifecycle | `Account`, `AccountStatusEnum`, `AccountTypeEnum`, `AccountClosedEvent` |
| `transaction` | Transfers, fees, validation, state machine | `Transaction`, `TransferDomainService`, `FeeCalculator`, `TransferValidator` |
| `customer` | Customer data (domain + persistence only for now) | `Customer`, `CustomerStatusEnum` |
| `shared` | Shared kernel | `Money`, `Email`, `CurrencyEnum` |
| `reporting` | Read-side composition (application layer) | `DashboardReadModel`, `AccountDashboardFacade` |

`fraud` and `ai` are infrastructure adapters, not business contexts. `fraud` is an anti-corruption layer around the external fraud check; `ai` is an inbound adapter that composes account and transaction use cases.

### Context map

```
transaction ──► account ──► shared
     │              ▲
     └──────────────┘
customer ───────────► shared
reporting ──► account + transaction (read only)
```

- `transaction` may depend on `account` (it needs accounts and account types).
- `account` never depends on `transaction`.
- `shared` depends on no context.
- `customer` stays independent.

### Package layout

```
com.atlas.bank.atlas_bank
├── domain
│   ├── account
│   │   ├── model/        Account
│   │   ├── enums/        AccountStatusEnum, AccountTypeEnum
│   │   ├── exception/    AccountNotFoundException, AccountNotActiveException,
│   │   │                 InsufficientFoundException
│   │   └── event/        AccountClosedEvent
│   ├── transaction
│   │   ├── model/        Transaction, TransfersContext
│   │   ├── enums/        TransactionStatusEnum, TransactionTypeEnum
│   │   ├── state/        TransactionState + states
│   │   ├── fee/          FeeCalculator + implementations
│   │   ├── validation/   TransferValidator, AccountStatusValidator,
│   │   │                 SufficientFoundValidator
│   │   ├── service/      TransferDomainService
│   │   ├── exception/    FraudCheckException
│   │   └── event/        TransactionExecutedEvent
│   ├── customer
│   │   ├── model/        Customer
│   │   └── enums/        CustomerStatusEnum
│   └── shared
│       ├── model/        Money, Email
│       └── enums/        CurrencyEnum
├── application
│   ├── account
│   │   ├── command/      CreateAccountCommand, CloseAccountCommand
│   │   ├── port/in/      Create/Get/List/CloseAccountUseCase
│   │   ├── port/out/     AccountRepositoryPort
│   │   └── service/      AccountServiceImpl, AuditableAccountService
│   ├── transaction
│   │   ├── command/      TransferMoneyCommand
│   │   ├── query/        GetAccountStatementQuery, TransactionReadModel
│   │   ├── port/in/      TransferMoneyUseCase, GetTransactionsByAccountUseCase
│   │   ├── port/out/     TransactionRepositoryPort, FraudCheckPort
│   │   ├── dto/          FraudCheckResult
│   │   ├── service/      TransferServiceImpl, TransactionProcessor,
│   │   │                 TransactionFactory, TransactionQueryService
│   │   └── validation/   FraudValidator
│   ├── customer
│   │   └── port/out/     CustomerRepositoryPort
│   └── reporting
│       ├── query/        DashboardReadModel
│       └── service/      AccountDashboardFacade
└── infrastructure
    ├── account
    │   ├── adapter/in/rest/          AccountController + dto/
    │   └── adapter/out/persistence/  AccountJpaEntity, mapper, adapter, repository
    ├── transaction
    │   ├── adapter/in/rest/          TransactionController + dto/
    │   ├── adapter/in/event/         AuditListener, NotificationListener
    │   ├── adapter/out/persistence/  TransactionJpaEntity, mapper, adapter, repository
    │   └── config/                   TransactionBeanConfig
    ├── customer
    │   └── adapter/out/persistence/  CustomerJpaEntity, mapper, adapter, repository
    ├── fraud
    │   └── adapter/out/external/     ExternalFraudCheckAdapter
    ├── ai
    │   ├── adapter/in/               AiChatController, AtlasBankTools
    │   └── config/                   AiAgentConfig
    └── shared
        ├── config/                   SecurityConfig
        ├── web/                      GlobalExceptionHandler
        └── persistence/              Money, Email (JPA embeddables)
```

### Rules enforced by ArchUnit

- `domain` must not depend on `application`, `infrastructure`, or Spring.
- `application` must not depend on `infrastructure` or Spring Security.
- `*Controller` classes live under `..adapter.in..`.
- `*UseCase` interfaces live under `..port.in..`; `port.in` and `port.out` contain interfaces only.
- Domain, application, and infrastructure contexts must be free of cycles.
- Context boundaries: `shared` does not depend on contexts; `account` does not depend on `transaction`; `customer` stays independent.

Run `./mvnw -q test` to verify all of the above.

## Build and run

```bash
./mvnw -q test
./mvnw spring-boot:run
```

The app starts on `:8080`.

- H2 console: `/h2-console`
- JDBC URL: `jdbc:h2:mem:atlasbank`
- User: `sa`, empty password

Keycloak is only needed for authenticated HTTP calls:

```bash
docker compose -f docker/docker-compose.yaml up -d
```

It exposes `:8181`. The compose file has no realm import, so a fresh instance needs the `atlas-bank` realm, client, and roles created manually.

## HTTP API

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/accounts` | Create an account |
| `GET` | `/api/v1/accounts` | List accounts |
| `GET` | `/api/v1/accounts/{id}` | Get an account |
| `GET` | `/api/v1/accounts/{id}/dashboard` | Account dashboard |
| `PATCH` | `/api/v1/accounts/{id}/close` | Close an account |
| `POST` | `/api/v1/transactions/transfer` | Transfer money |
| `GET` | `/api/v1/transactions/{id}/transactions` | Account statement |
| `POST` | `/api/v1/ai/chat` | AI assistant |

Errors are returned as `ProblemDetail` (404 for missing accounts, 422 for insufficient/inactive/fraud, 400 for validation).

## Notes

- H2 is in-memory with `ddl-auto: create-drop`; data resets on every restart and there are no migrations.
- `spring.jpa.show-sql` is enabled; use `-q` if you want less noise.
- Fraud checking is a local stub: amounts over `1,000,000` are blocked.
- Customers currently exist only as domain + persistence; there is no customer use case, service, or REST endpoint yet.
