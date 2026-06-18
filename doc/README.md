# FiscalizAI API - Documentação

## Visão Geral do Projeto

**FiscalizAI** é uma API RESTful desenvolvida para empoderar cidadãos a fiscalizar e reportar problemas urbanos em suas cidades. O sistema permite que usuários registrem ocorrências como buracos nas ruas, iluminação pública defeituosa, acúmulo de lixo, problemas de sinalização, entre outros, contribuindo para uma gestão municipal mais transparente e eficiente.

### Objetivo

Facilitar a comunicação entre cidadãos e autoridades municipais, permitindo que problemas urbanos sejam documentados, categorizados e acompanhados de forma estruturada e acessível.

### Tecnologias Utilizadas

- **Framework**: Quarkus 3.36.1
- **Linguagem**: Java 21
- **Banco de Dados**: PostgreSQL
- **ORM**: Hibernate ORM com Panache
- **API REST**: Quarkus REST (RESTEasy Reactive)
- **Documentação**: OpenAPI/Swagger UI
- **Validação**: Hibernate Validator

## Como Acessar

### Ambiente de Desenvolvimento
- **Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/q/swagger-ui`
- **OpenAPI Spec**: `http://localhost:8080/q/openapi`

### Ambiente de Produção
- **Swagger UI**: Disponível em `/swagger-ui`
- **OpenAPI Spec**: Disponível em `/q/openapi`

## Estrutura da Documentação

1. [Glossário de Termos](./GLOSSARY.md) - Tradução dos termos em inglês para português
2. [Endpoints da API](./API_ENDPOINTS.md) - Documentação detalhada de todos os endpoints
3. [Modelos de Dados](./DATA_MODELS.md) - Estrutura das entidades e DTOs
4. [Guia de Início Rápido](./QUICKSTART.md) - Como começar a usar a API

## Funcionalidades Principais

### 1. Gestão de Ocorrências (Issues)
Permite criar, visualizar, atualizar e excluir reportes de problemas urbanos.

### 2. Categorização
Sistema de categorias para classificar diferentes tipos de problemas urbanos.

### 3. Severidade e Status
Acompanhamento do grau de severidade e status atual de cada ocorrência.

### 4. Validação de Localização
Integração com API de CEP para validação de endereços brasileiros.

### 5. Confirmação Comunitária
Sistema de confirmação onde múltiplos usuários podem validar uma ocorrência.

## Arquitetura

```
fiscalizai-api/
├── src/main/java/io/fiscalizai/
│   ├── config/          # Configurações da aplicação
│   ├── controller/      # Clientes REST externos
│   ├── model/           # Entidades e DTOs
│   │   ├── entity/      # Entidades JPA
│   │   ├── dto/         # Data Transfer Objects
│   │   └── messages/    # Mensagens i18n
│   └── rest/            # Endpoints REST
└── src/main/resources/
    ├── application.properties
    └── import.sql       # Dados iniciais
```

## Suporte e Contribuição

Para reportar problemas ou sugerir melhorias, consulte o repositório do projeto.

## Licença

Consulte o arquivo LICENSE na raiz do projeto.
