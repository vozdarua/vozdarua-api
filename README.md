# Voz da Rua API

A REST API for managing civic issues and community reports, built with Quarkus.

## Overview

Voz da Rua is a citizen reporting platform that empowers citizens to monitor and report urban infrastructure issues in their cities. Users can report problems like potholes, broken street lights, garbage accumulation, traffic signs issues, and other civic problems.

## Tech Stack

- **Framework**: Quarkus 3.36.1
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Hibernate with Panache
- **API**: Jakarta REST (JAX-RS)
- **API Documentation**: SmallRye OpenAPI / Swagger UI

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.x
- PostgreSQL database

### Running in Development Mode

```bash
./mvnw quarkus:dev
```

The API will be available at `http://localhost:8080`

**Quick Links:**
- **Swagger UI**: `http://localhost:8080/q/swagger-ui/` - Interactive API documentation
- **Dev UI**: `http://localhost:8080/q/dev/` - Quarkus development console

## Documentation

Complete documentation is available in the `/doc` folder:

- **[Project Overview](./doc/README.md)** - Detailed project information and architecture
- **[API Endpoints](./doc/API_ENDPOINTS.md)** - Complete endpoint documentation
- **[Data Models](./doc/DATA_MODELS.md)** - Entity structures and relationships
- **[Glossary](./doc/GLOSSARY.md)** - Portuguese translations (Issue → Ocorrência, etc.)
- **[Quick Start Guide](./doc/QUICKSTART.md)** - Step-by-step setup and usage examples

## Building for Production

```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

For native executable:
```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Testing

```bash
./mvnw test
```

## License

This project is licensed under the terms specified in the project license file.
