# AGENTS.md

## Build and run

- **Java 25 is required** (`<java.version>25</java.version>`). Use the wrapper `./mvnw` (Maven 3.10.0, script-only wrapper — no wrapper jar).
- Full verification is `./mvnw -q test`. There is no CI, lint, or formatter config in the repo, so compilation + tests are the gate.
- Single test: `./mvnw test -Dtest=MoneyDomainTest` (also works for `HexagonalArchitectureTest`, etc.).
- Run the app: `./mvnw spring-boot:run` on `:8080`. H2 console: `/h2-console`, JDBC `jdbc:h2:mem:atlasbank`, user `sa`, empty password.
- Keycloak is only needed for authenticated HTTP calls: `docker compose -f docker/docker-compose.yaml up -d` exposes `:8181`. Tests and app startup do not contact it (JWT issuer metadata is fetched lazily). The compose file has **no realm import or volume**, so a fresh instance needs the `atlas-bank` realm, client, and roles created manually.

## Architecture (enforced by ArchUnit)

- Package root is `com.atlas.bank.atlas_bank` (underscore, unlike the artifact's hyphen).
- Code is organized **by bounded context inside each layer**:
  - `domain/<context>/{model,enums,exception,event,service,validation,fee,state}`
  - `application/<context>/{command,query,port/in,port/out,service,validation}`
  - `infrastructure/<context>/{adapter/in,adapter/out,config}`
- Contexts: `account`, `transaction`, `customer`, `shared`. `reporting` is a read-side context under `application/`; `fraud` and `ai` are infrastructure adapters, not business contexts.
- `domain/` is framework-free (no Spring at all); `application/` holds use cases, commands/queries, ports, and services; `infrastructure/` implements the ports. `infrastructure/shared` holds cross-cutting config (`SecurityConfig`), the global exception handler, and the JPA `@Embeddable` twins.
- Dependency direction: `transaction → account → shared`, `customer → shared`, `shared` depends on no context. `application/reporting` may read from `account` and `transaction`. Enforced by ArchUnit.
- `src/test/.../archtest` enforces: domain ↛ application/infrastructure/Spring; application ↛ infrastructure/Spring Security; `*Controller` only in `..adapter.in..`; `*UseCase` only in `..port.in..`; `port.in`/`port.out` are interfaces; per-layer context cycles are forbidden; context boundaries (`shared`, `account`, `customer`) are checked. Violating these fails `./mvnw test`.
- Domain collaborators (validators, fee calculators, `TransferDomainService`) have no Spring annotations. Register new ones in `infrastructure/transaction/config/TransactionBeanConfig`. Validators and fee calculators are injected as `List<...>`; `@Order` controls validator execution (account status → funds → fraud) and fee selection (first matching calculator wins, `DefaultFeeCalculator` is the fallback).
- `AuditableAccountService` is `@Primary` and delegates to `accountServiceImpl` via `@Qualifier`. Inject the use-case interfaces, not `AccountServiceImpl` directly.
- Persistence deliberately duplicates value objects: `infrastructure.shared.persistence.Money`/`Email` are JPA `@Embeddable` twins of `domain.shared.model.Money`/`Email`. `AccountPersistenceMapper` uses fully-qualified names to disambiguate. `TransactionJpaEntity` uses the same persistence `Money` (currency stored as a string); `TransactionPersistenceMapper` converts to/from the domain `Money`.
- MapStruct mappers (`AccountMapper`, `TransactionMapper`) use `componentModel = "spring"`; implementations are generated into `target/generated-sources`. Keep the Lombok → MapStruct → `lombok-mapstruct-binding` processor order from `pom.xml`.
- `JpaTransactionRepositoryAdapter.save` publishes `Transaction.domainEvents` and then clears them. Add new events to `domainEvents`; `AuditListener` and `NotificationListener` (in `infrastructure/transaction/adapter/in/event`) are `@TransactionalEventListener(AFTER_COMMIT)` and currently just log.
- Fraud checking is a local stub in `infrastructure/fraud/adapter/out/external/ExternalFraudCheckAdapter`: amounts over 1,000,000 are blocked, everything else is allowed. No external service is involved.
- Customers exist only as domain + persistence (`CustomerRepositoryPort`, `JpaCustomerRepositoryAdapter`); there is no customer use case, service, or REST endpoint yet.

## Data and HTTP

- H2 is in-memory with `ddl-auto: create-drop`; data resets on every restart and there are no migrations. `spring.jpa.show-sql` is on, so prefer `-q` for less noise.
- Endpoints: `POST/GET /api/v1/accounts`, `GET /api/v1/accounts/{id}`, `GET /api/v1/accounts/{id}/dashboard`, `PATCH /api/v1/accounts/{id}/close` (ADMIN), `POST /api/v1/transactions/transfer`, `GET /api/v1/transactions/{id}/transactions`, `POST /api/v1/ai/chat`.
- Errors are returned as `ProblemDetail` by `GlobalExceptionHandler` in `infrastructure/shared/web` (404 for missing accounts, 422 for insufficient/inactive/fraud/non-zero balance on close, 400 for validation and invalid transfers, 403 for access denied, 409 for duplicates and concurrent modifications). It extends `ResponseEntityExceptionHandler`, so standard MVC errors keep their 4xx status.
- Auth: the JWT converter maps `realm_access.roles` to authorities, adding the `ROLE_` prefix when missing, so Keycloak roles `ADMIN`/`USER` work with `hasRole(...)`.
- Account ownership: `AccountAccessGuard` (`infrastructure/account/adapter/in/security`) lets ADMIN access any account and other users only accounts whose email equals the JWT `email` claim (403 otherwise, including unknown ids). Every inbound adapter that touches an account (REST controllers and `AtlasBankTools`) must call it. `/api/v1/ai/**` requires ADMIN or USER; the close-account tool requires ADMIN.
- Concurrency: transfers and account closing load accounts with `AccountRepositoryPort.findByIdForUpdate` (pessimistic write lock, ascending id order). Lock failures map to 409.
