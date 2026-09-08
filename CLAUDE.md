# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Voz da Rua is a citizen reporting platform API built with Quarkus 3.36.1 and Java 21. It enables citizens to report and track urban infrastructure issues (potholes, broken street lights, garbage accumulation, etc.). The API uses PostgreSQL with Hibernate Panache for data access, and integrates with Cloudflare R2 for image storage and BrasilAPI for Brazilian postal code validation.

## Development Commands

### Running the Application

**Development mode** (with live reload):
```bash
./mvnw quarkus:dev
```

Access points:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/q/swagger-ui/`
- Dev UI: `http://localhost:8080/q/dev/`

### Testing

Run all tests:
```bash
./mvnw test
```

Run a single test class:
```bash
./mvnw test -Dtest=IssueResourceTest
```

Run a specific test method:
```bash
./mvnw test -Dtest=IssueResourceTest#testCreateIssue
```

### Building

Package for JVM:
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

Build native executable (requires GraalVM or container):
```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Architecture

### Package Structure

```
src/main/java/io/vozdarua/
├── config/              # Application configuration (locale, CDI producers)
├── controller/
│   └── restclient/      # External REST clients (BrasilAPI for CEP validation)
├── model/
│   ├── entity/          # JPA entities (Panache-based)
│   ├── dto/             # Data Transfer Objects
│   └── messages/        # i18n message bundles
└── rest/                # JAX-RS REST endpoints (Resources)
```

### Core Entities

All entities extend `PanacheEntity` which provides:
- `id` (Long) as primary key
- Static finder methods: `findById()`, `listAll()`, `find()`, etc.
- Active record pattern: `entity.persist()`, `entity.delete()`

Main entities:
- **Issue**: Urban problem reports with relationships to Category, Severity, Status, User (reporter), Address, and Image (photo)
- **Category**: Classification of issue types
- **Severity**: Issue severity levels
- **Status**: Issue lifecycle states
- **User**: System users who report issues
- **Address**: Location information for issues
- **Image**: Photo evidence stored in Cloudflare R2

### Data Access Pattern

Uses Hibernate Panache active record pattern:
```java
// Finding
Issue issue = Issue.findById(id);
List<Issue> all = Issue.listAll();
List<Issue> bySeverity = Issue.list("severity", severity);

// Persisting
issue.persist();

// Deleting
issue.delete();
```

### REST Resources

Resources follow JAX-RS conventions:
- Use `@Path` for endpoint mapping
- Return `Response` objects with appropriate status codes
- Use `@Consumes` and `@Produces` for content negotiation
- Leverage Quarkus REST (RESTEasy Reactive) features

### Internationalization (i18n)

The API supports Portuguese (pt-BR, default) and English (en):

- Message bundles: `src/main/resources/messages/AppMessages_{locale}.properties`
- Locale detection: Based on `Accept-Language` header
- Inject messages via: `@Inject @RequestLocale AppMessages messages`
- Default locale: Portuguese (pt-BR) if not specified or unsupported

When adding user-facing messages, add them to both property files.

### External Integrations

**BrasilAPI Client**: Validates Brazilian postal codes (CEP)
- Interface: `io.vozdarua.controller.restclient.BrazilApiClient`
- Returns reactive `Uni<CepLocation>` for async processing
- Base URI: `https://brasilapi.com.br/api/cep/v1`

**Cloudflare R2 (S3-compatible)**: Image storage
- Configuration via `application.properties` with profile-specific settings
- Bucket name: `cloudflare.r2.bucket-name`
- Public URL: `cloudflare.r2.public-url`
- Environment variables needed for production: `R2_ACCESS_KEY_ID`, `R2_SECRET_ACCESS_KEY`

### Configuration Profiles

Quarkus uses profiles (dev, test, prod) via `application.properties`:

**Dev profile** (`%dev.`):
- Database: Local PostgreSQL (localhost:5432/vozdarua_db)
- Schema: `drop-and-create` (resets on restart)
- Imports `import.sql` for seed data

**Test profile** (`%test.`):
- Same database setup as dev
- Schema: `drop-and-create` for clean test state

**Prod profile** (`%prod.`):
- Database: Environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`)
- Schema: `update` (preserves data)
- SSL required for database connection
- Swagger UI enabled at `/swagger-ui`

### Database Setup

Requires PostgreSQL. For development:
```bash
# Docker option
docker run -d --name vozdarua-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=secret \
  -e POSTGRES_DB=vozdarua_db \
  -p 5432:5432 \
  postgres:16
```

## Testing Infrastructure

Tests use:
- **Quarkus Test**: `@QuarkusTest` annotation for integration tests
- **REST Assured**: For HTTP endpoint testing
- **PostgreSQL**: Tests run against real database (not H2/mocks)

CI/CD runs via GitHub Actions (`.github/workflows/tests.yml`) with PostgreSQL service container.

## Glossary

The project uses English in code but targets Brazilian Portuguese users:

| Code Term | Portuguese Equivalent | Meaning |
|-----------|----------------------|---------|
| Issue | Ocorrência | Urban problem report |
| Reporter | Denunciante/Relator | User who reported the issue |
| Category | Categoria | Type classification |
| Severity | Severidade/Gravidade | Severity level |
| Status | Status/Estado | Current state |
| confirmIssue | Confirmações | Community confirmation counter |
| confirmResolve | Confirmações de resolução | Community resolution counter |

See `doc/GLOSSARY.md` for complete term mappings.

## Documentation

Comprehensive docs in `/doc`:
- `README.md`: Project overview (Portuguese)
- `API_ENDPOINTS.md`: Complete endpoint documentation
- `DATA_MODELS.md`: Entity structures and relationships
- `GLOSSARY.md`: English-Portuguese term mappings
- `QUICKSTART.md`: Step-by-step usage examples

## Code Conventions

- Entities use public fields (Panache style, not getters/setters)
- Use `@CreationTimestamp` and `@UpdateTimestamp` for audit fields
- REST resources inject dependencies via CDI (`@Inject`)
- Use reactive types (`Uni<T>`) for async operations with external services
- Validation via Hibernate Validator annotations on entities/DTOs
