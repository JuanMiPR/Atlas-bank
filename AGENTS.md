# AGENTS.md

## Build and run

- **Java 25 is required** (`<java.version>25</java.version>`). Use the wrapper `./mvnw` (Maven 3.10.0, script-only wrapper — no wrapper jar).
- Full verification is `./mvnw -q test`. There is no CI, lint, or formatter config in the repo, so compilation + tests are the gate.
- Single test: `./mvnw test -Dtest=MoneyDomainTest` (also works for `HexagonalArchitectureTest`, etc.).
- Run the app: `./mvnw spring-boot:run` on `:8080`. H2 console: `/h2-console`, JDBC `jdbc:h2:mem:atlasbank`, user `sa`, empty password.
- Keycloak is only needed for authenticated HTTP calls: `docker compose -f docker/docker-compose.yaml up -d` exposes `:8181`. Tests and app startup do not contact it (JWT issuer metadata is fetched lazily). The compose file has **no realm import or volume**, so a fresh instance needs the `atlas-bank` realm, client, and roles created manually.

## Architecture (enforced by ArchUnit)

- Package root is `com.atlas.bank.atlas_bank` (underscore, unlike the artifact's hyphen).
- `domain/` is framework-free (no Spring at all); `application/` holds use cases, commands/queries, ports, and services; `infrastructure/adapter/in|out` holds REST, JPA, fraud, and listeners; wiring lives in `infrastructure/config`.
- `src/test/.../archtest` enforces: domain ↛ application/infrastructure/Spring; application ↛ infrastructure/Spring Security; `*Controller` only in `adapter.in.rest`; `*UseCase` only in `port.in`; `port.in`/`port.out` are interfaces; no package cycles. Violating these fails `./mvnw test`.
- Domain collaborators (validators, fee calculators, `TransferDomainService`) have no Spring annotations. Register new ones in `DomainBeanConfig`. Validators and fee calculators are injected as `List<...>`; `@Order` controls validator execution (account status → funds → fraud) and fee selection (first matching calculator wins, `DefaultFeeCalculator` is the fallback).
- `AuditableAccountService` is `@Primary` and delegates to `accountServiceImpl` via `@Qualifier`. Inject the use-case interfaces, not `AccountServiceImpl` directly.
- Persistence deliberately duplicates value objects: `infrastructure.adapter.out.persistence.Money`/`Email` are JPA `@Embeddable` twins of `domain.model.shared.Money`/`Email`. `AccountPersistenceMapper` uses fully-qualified names to disambiguate. Transactions, by contrast, persist the domain `Money` directly — don't assume one shared embeddable.
- MapStruct mappers (`AccountMapper`, `TransactionMapper`) use `componentModel = "spring"`; implementations are generated into `target/generated-sources`. Keep the Lombok → MapStruct → `lombok-mapstruct-binding` processor order from `pom.xml`.
- `JpaTransactionRepositoryAdapter.save` publishes `Transaction.domainEvents` and then clears them. Add new events to `domainEvents`; `AuditListener` and `NotificationListener` currently just log.
- Fraud checking is a local stub in `ExternalFraudCheckAdapter`: amounts over 1,000,000 are blocked, everything else is allowed. No external service is involved.
- Customers exist only as domain + persistence (`CustomerRepositoryPort`, `JpaCustomerRepositoryAdapter`); there is no customer use case, service, or REST endpoint yet.

## Data and HTTP

- H2 is in-memory with `ddl-auto: create-drop`; data resets on every restart and there are no migrations. `spring.jpa.show-sql` is on, so prefer `-q` for less noise.
- Endpoints: `POST/GET /api/v1/accounts`, `GET /api/v1/accounts/{id}`, `GET /api/v1/accounts/{id}/dashboard`, `POST /api/v1/transactions/transfer`, `GET /api/v1/transactions/{id}/transactions`.
- Errors are returned as `ProblemDetail` by `GlobalExceptionHandler` (404 for missing accounts, 422 for insufficient/inactive/fraud, 400 for validation).
- Auth gotcha: `SecurityConfig` uses `hasRole("ADMIN"/"USER")`, but the JWT converter copies `realm_access.roles` verbatim without adding `ROLE_`. Tokens therefore need authorities literally named `ROLE_ADMIN`/`ROLE_USER` unless the converter or the checks are aligned.
