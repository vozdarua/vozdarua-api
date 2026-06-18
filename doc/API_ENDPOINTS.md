# Documentação dos Endpoints da API

Esta documentação apresenta todos os endpoints disponíveis na API FiscalizAI, organizados por recurso.

## Índice

1. [Issues (Ocorrências)](#1-issues-ocorrências)
2. [Users (Usuários)](#2-users-usuários)
3. [Categories (Categorias)](#3-categories-categorias)
4. [Status](#4-status)
5. [Severity (Severidade)](#5-severity-severidade)
6. [Location (Localização)](#6-location-localização)

---

## 1. Issues (Ocorrências)

**Base Path**: `/issues`

**Descrição**: Gerencia as ocorrências de problemas urbanos reportados pelos cidadãos. Permite criar, visualizar, atualizar e excluir reportes, além de filtrá-los por diversos critérios.

**Tradução**: Ocorrências - problemas urbanos como buracos, iluminação defeituosa, acúmulo de lixo, etc.

### 1.1. Listar Todas as Ocorrências

```
GET /issues
```

**Descrição**: Retorna todas as ocorrências cadastradas no sistema.

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "description": "Buraco na rua",
    "confirmIssue": 5,
    "photo": "https://example.com/photo.jpg",
    "address": "Rua das Flores, 123",
    "reporter": {...},
    "category": {...},
    "severity": {...},
    "status": {...}
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`
```json
{
  "message": "Nenhuma ocorrência encontrada"
}
```

---

### 1.2. Buscar Ocorrência por ID

```
GET /issues/{id}
```

**Parâmetros**:
- `id` (path) - ID da ocorrência

**Descrição**: Retorna os detalhes de uma ocorrência específica.

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.3. Criar Nova Ocorrência

```
POST /issues
```

**Descrição**: Cria uma nova ocorrência no sistema. Requer que o usuário denunciante esteja cadastrado.

**Body (JSON)**:
```json
{
  "description": "Iluminação pública queimada na esquina",
  "photo": "base64_encoded_image_or_url",
  "address": "Av. Principal, 456",
  "reporter": {
    "id": 1
  },
  "category": {
    "id": 2
  },
  "severity": {
    "id": 3
  },
  "status": {
    "id": 1
  }
}
```

**Campos Obrigatórios**:
- `reporter.id` - ID do usuário que está reportando
- `description` - Descrição da ocorrência

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `404 NOT FOUND` - Usuário, severidade ou status não encontrado

---

### 1.4. Atualizar Ocorrência

```
PUT /issues/{id}
```

**Parâmetros**:
- `id` (path) - ID da ocorrência

**Descrição**: Atualiza os dados de uma ocorrência existente.

**Body (JSON)**: Mesma estrutura do POST

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.5. Excluir Ocorrência

```
DELETE /issues/{id}
```

**Parâmetros**:
- `id` (path) - ID da ocorrência

**Descrição**: Remove permanentemente uma ocorrência do sistema.

**Resposta de Sucesso**: `204 NO CONTENT`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.6. Confirmar Ocorrência

```
PUT /issues/confirm/{id}
```

**Parâmetros**:
- `id` (path) - ID da ocorrência

**Descrição**: Incrementa o contador de confirmações da ocorrência. Usado quando outros cidadãos confirmam que o problema existe.

**Resposta de Sucesso**: `200 OK`
```json
{
  "id": 1,
  "confirmIssue": 6,
  ...
}
```

**Resposta de Erro**: `404 NOT FOUND`

---

### 1.7. Listar Ocorrências por Categoria

```
GET /issues/category/{categoryId}
```

**Parâmetros**:
- `categoryId` (path) - ID da categoria

**Descrição**: Retorna todas as ocorrências de uma categoria específica (ex: todas ocorrências de "Infraestrutura").

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.8. Listar Ocorrências por Status

```
GET /issues/status/{statusId}
```

**Parâmetros**:
- `statusId` (path) - ID do status

**Descrição**: Filtra ocorrências por status (ex: todas as "Pendentes" ou "Resolvidas").

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.9. Listar Ocorrências por Severidade

```
GET /issues/severity/{severityId}
```

**Parâmetros**:
- `severityId` (path) - ID da severidade

**Descrição**: Filtra ocorrências por nível de severidade (ex: todas as "Críticas").

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.10. Listar Ocorrências por Denunciante

```
GET /issues/reporter/{reporterId}
```

**Parâmetros**:
- `reporterId` (path) - ID do usuário denunciante

**Descrição**: Retorna todas as ocorrências reportadas por um usuário específico.

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

## 2. Users (Usuários)

**Base Path**: `/user`

**Descrição**: Gerencia os usuários do sistema (cidadãos que reportam ocorrências).

**Tradução**: Usuários/Cidadãos cadastrados

### 2.1. Criar Usuário

```
POST /user
```

**Descrição**: Cadastra um novo usuário no sistema.

**Body (JSON)**:
```json
{
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "phone": "+55 11 98765-4321"
}
```

**Resposta de Sucesso**: `201 CREATED`

---

## 3. Categories (Categorias)

**Base Path**: `/categories`

**Descrição**: Fornece a lista de categorias disponíveis para classificação de ocorrências (ex: Infraestrutura, Limpeza Urbana, Iluminação Pública).

**Tradução**: Categorias de problemas urbanos

### 3.1. Listar Categorias

```
GET /categories
```

**Descrição**: Retorna todas as categorias disponíveis no sistema.

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Infraestrutura"
  },
  {
    "id": 2,
    "name": "Iluminação Pública"
  },
  {
    "id": 3,
    "name": "Limpeza Urbana"
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`

---

## 4. Status

**Base Path**: `/status`

**Descrição**: Fornece a lista de status possíveis para uma ocorrência (ex: Pendente, Em Análise, Resolvida).

**Tradução**: Status/Estado da ocorrência

### 4.1. Listar Status

```
GET /status
```

**Descrição**: Retorna todos os status disponíveis no sistema.

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Pendente"
  },
  {
    "id": 2,
    "name": "Em Análise"
  },
  {
    "id": 3,
    "name": "Resolvida"
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`

---

## 5. Severity (Severidade)

**Base Path**: `/severity`

**Descrição**: Fornece a lista de níveis de severidade para classificar a gravidade das ocorrências (ex: Baixa, Média, Alta, Crítica).

**Tradução**: Severidade/Gravidade da ocorrência

### 5.1. Listar Severidades

```
GET /severity
```

**Descrição**: Retorna todos os níveis de severidade disponíveis.

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Baixa"
  },
  {
    "id": 2,
    "name": "Média"
  },
  {
    "id": 3,
    "name": "Alta"
  },
  {
    "id": 4,
    "name": "Crítica"
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`

---

## 6. Location (Localização)

**Base Path**: `/location`

**Descrição**: Serviço de validação e busca de endereços usando CEP (Código de Endereçamento Postal brasileiro). Integra-se com a API Brasil API para obter dados precisos de localização.

**Tradução**: Localização/Endereço

### 6.1. Buscar Endereço por CEP

```
GET /location/cep/{cep}
```

**Parâmetros**:
- `cep` (path) - CEP no formato 00000-000 ou 00000000

**Descrição**: Busca informações de endereço a partir do CEP fornecido. Retorna rua, bairro, cidade, estado, etc.

**Exemplo de Requisição**:
```
GET /location/cep/01310-100
```

**Resposta de Sucesso**: `200 OK`
```json
{
  "cep": "01310-100",
  "state": "SP",
  "city": "São Paulo",
  "neighborhood": "Bela Vista",
  "street": "Avenida Paulista",
  "service": "viacep"
}
```

**Respostas de Erro**:
- `404 NOT FOUND` - CEP não encontrado
```json
{
  "message": "CEP 99999-999 não encontrado"
}
```

- `502 BAD GATEWAY` - Serviço de CEP indisponível
```json
{
  "message": "Serviço de consulta de CEP temporariamente indisponível"
}
```

---

## Swagger UI

Para testar todos os endpoints interativamente, acesse a interface do Swagger UI:

- **Desenvolvimento**: http://localhost:8080/q/swagger-ui
- **Produção**: https://seu-dominio.com/swagger-ui

A interface Swagger permite:
- Visualizar todos os endpoints disponíveis
- Testar requisições diretamente no navegador
- Ver exemplos de requisição e resposta
- Consultar os schemas de dados
- Verificar códigos de resposta HTTP

---

## Notas Importantes

### Autenticação
Atualmente a API não possui autenticação implementada. Em produção, recomenda-se adicionar:
- JWT (JSON Web Tokens)
- OAuth 2.0
- API Keys

### Internacionalização
A API suporta múltiplos idiomas através do header `Accept-Language`:
- `pt-BR` - Português do Brasil (padrão)
- `pt` - Português
- `en` - Inglês

### Formato de Dados
- Todas as requisições e respostas usam JSON
- Datas seguem formato ISO 8601
- IDs são do tipo Long (números inteiros)

### Rate Limiting
Não há limite de requisições implementado atualmente. Recomenda-se implementar em produção.
