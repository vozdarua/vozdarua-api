# FiscalizAI API

A REST API for managing civic issues and community reports, built with Quarkus.

## Overview

FiscalizAI is a citizen reporting platform that allows users to report and track public infrastructure issues in their communities. The API provides endpoints for managing issues, users, categories, and locations.

## Tech Stack

- **Framework**: Quarkus 3.36.1
- **Language**: Java 25
- **Database**: PostgreSQL
- **ORM**: Hibernate with Panache
- **API**: Jakarta REST (JAX-RS)
- **Serialization**: Jackson
- **Validation**: Hibernate Validator
- **API Documentation**: SmallRye OpenAPI

## Core Features

### Issue Management
- Create, read, update, and delete issues
- Filter issues by category, status, severity, or reporter
- Track issue confirmation count
- Attach photos to issues
- Associate issues with geographic addresses

### User Management
- Phone-based user authentication
- User registration and management

### Location Services
- Address management with CEP (Brazilian postal code) integration
- Integration with Brazil API for address lookup

### Categories
- Categorize issues (infrastructure, environment, safety, etc.)

## Domain Model

### Entities

- **Issue**: Core entity representing a civic report
  - Description, severity, status
  - Category, reporter, address
  - Photo attachment
  - Confirmation counter
  - Timestamps (created/updated)

- **FiscalizaiUser**: Platform users
  - Phone number (unique identifier)
  - Password
  - Timestamps

- **Category**: Issue classification

- **Address**: Geographic location information

- **Image**: Photo attachments for issues

### Enums

- **Severity**: Issue severity levels
- **Status**: Issue lifecycle states

## Getting Started

### Prerequisites

- Java 25 or later
- Maven 3.x
- PostgreSQL database

### Database Configuration

Configure your PostgreSQL connection in `src/main/resources/application.properties`:

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=secret
quarkus.datasource.jdbc.url=jdbc:postgresql://127.0.0.1:5432/fiscalizai_db
```

### Running in Development Mode

Start the application with live reload enabled:

```bash
./mvnw quarkus:dev
```

The API will be available at `http://localhost:8080`

**Quick Links:**
- **Swagger UI**: `http://localhost:8080/q/swagger-ui/` - Interactive API documentation and testing
- **Dev UI**: `http://localhost:8080/q/dev/` - Quarkus development console
- **OpenAPI Spec**: `http://localhost:8080/q/openapi` - OpenAPI specification (JSON)

### API Documentation with Swagger

The API comes with built-in Swagger UI for interactive documentation and testing. Once the application is running, navigate to:

**`http://localhost:8080/q/swagger-ui/`**

Swagger UI provides:
- Complete API endpoint documentation
- Request/response schemas
- Interactive "Try it out" functionality to test endpoints
- Model definitions for all entities and DTOs
- Authentication testing capabilities

## API Endpoints

### Issues

- `GET /issues` - List all issues
- `GET /issues/{id}` - Get issue by ID
- `POST /issues` - Create new issue
- `PUT /issues/{id}` - Update issue
- `DELETE /issues/{id}` - Delete issue
- `GET /issues/category/{categoryId}` - List issues by category
- `GET /issues/status/{status}` - List issues by status
- `GET /issues/severity/{severity}` - List issues by severity
- `GET /issues/reporter/{reporterId}` - List issues by reporter

### Users

- User management endpoints (see `FiscalizaiUserResource`)

### Locations

- Address and CEP lookup services (see `LocationResource`)

## Building for Production

### Standard JAR

```bash
./mvnw package
```

Run with:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

### Uber JAR

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

Run with:
```bash
java -jar target/*-runner.jar
```

### Native Executable

With GraalVM installed:
```bash
./mvnw package -Dnative
```

Or using container build:
```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Run with:
```bash
./target/fiscalizai-api-1.0.0-SNAPSHOT-runner
```

## Testing

Run tests with:
```bash
./mvnw test
```

## Project Structure

```
src/
├── main/
│   ├── java/io/fiscalizai/
│   │   ├── controller/
│   │   │   ├── converter/    # JPA converters
│   │   │   └── restclient/   # External API clients
│   │   ├── model/
│   │   │   ├── dto/          # Data transfer objects
│   │   │   └── entity/       # JPA entities
│   │   └── rest/             # REST endpoints
│   └── resources/
│       ├── application.properties
│       └── import.sql
└── test/
    └── java/io/fiscalizai/
        └── rest/             # REST endpoint tests
```

## Development

The project uses Hibernate Panache for simplified data access, providing active record pattern on entities. All entities extend `PanacheEntity` which provides auto-generated ID and common persistence methods.

## License

This project is licensed under the terms specified in the project license file.
