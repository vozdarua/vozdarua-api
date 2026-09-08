# Documentação dos Endpoints da API

Esta documentação apresenta todos os endpoints disponíveis na API Voz da Rua, organizados por recurso.

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

### 1.1. Listar Ocorrências (paginado, com filtros combináveis)

```
GET /issues
```

**Descrição**: Retorna as ocorrências cadastradas, paginadas. Todos os filtros abaixo são opcionais e combináveis entre si (ex: `categoryId` + `cityId` numa única chamada).

**Query Params**:
- `cityId` (long, opcional) - filtra por cidade resolvida (`address.cityRef`)
- `stateId` (long, opcional) - filtra por estado resolvido (`address.stateRef`)
- `neighborhood` (string, opcional) - filtra por bairro (`address.neighborhood`, igualdade exata)
- `categoryId` (long, opcional)
- `statusId` (long, opcional)
- `severityId` (long, opcional)
- `page` (int, opcional, default `0`)
- `size` (int, opcional, default `20`, máximo `500`)

**Resposta de Sucesso**: `200 OK` (sempre — sem resultados retorna `content: []`, não 404)
```json
{
  "content": [
    {
      "id": 1,
      "description": "Buraco na rua",
      "confirmIssue": 5,
      "photo": {...},
      "address": {...},
      "reporter": {...},
      "category": {...},
      "severity": {...},
      "status": {...}
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
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

> Categoria, status e severidade não têm mais endpoints dedicados
> (`/category/{id}`, `/status/{id}`, `/severity/{id}`) — use os query params
> `categoryId`/`statusId`/`severityId` de **1.1**, combináveis com os demais.

### 1.7. Métricas Agregadas por Cidade

```
GET /issues/metrics
```

**Descrição**: Retorna contagens agregadas de ocorrências de uma cidade — total, breakdown por status, top categorias, top bairros e breakdown por severidade. Usado pelo painel de métricas do mapa (`CityMetricsSheet`/`CityMetricsSidebar`) em vez de calcular tudo no client.

**Query Params**:
- `cityId` (long, **obrigatório**)
- `neighborhood` (string, opcional) - restringe a um bairro específico

**Resposta de Sucesso**: `200 OK`
```json
{
  "cityId": 42,
  "total": 87,
  "byStatus": [{"name": "Aberto", "count": 30}, {"name": "Resolvido", "count": 57}],
  "byCategory": [{"name": "Buraco no asfalto", "count": 20}],
  "byNeighborhood": [{"name": "Centro", "count": 15}],
  "bySeverity": [{"name": "Alto", "count": 10}, {"name": "Médio", "count": 40}, {"name": "Baixo", "count": 37}]
}
```
`byCategory`/`byNeighborhood` retornam no máximo os 6 valores com mais ocorrências; `byStatus`/`bySeverity` retornam todos os valores que têm pelo menos uma ocorrência.

**Resposta de Erro**: `400 BAD REQUEST` se `cityId` não for informado.

---

### 1.8. Listar Ocorrências por Denunciante

```
GET /issues/reporter/{reporterId}
```

**Acesso**: Apenas `ADMIN`

**Parâmetros**:
- `reporterId` (path) - ID do usuário denunciante

**Descrição**: Retorna todas as ocorrências reportadas por um usuário específico.

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 1.9. Upload de Imagem para Cloudflare R2

```
POST /issues/image/upload
```

**Content-Type**: `multipart/form-data`

**Descrição**: Faz upload de uma imagem para o Cloudflare R2 (S3-compatible storage) e retorna os dados da imagem persistida no banco de dados. A URL pública da imagem pode ser usada no campo `photo` ao criar ou atualizar uma ocorrência.

**Parâmetros (Form Data)**:
- `file` (file) - Arquivo de imagem a ser enviado

**Exemplo de Requisição**:
```bash
curl -X POST http://localhost:8080/issues/image/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/image.jpg"
```

**Resposta de Sucesso**: `200 OK`
```json
{
  "id": 1,
  "name": "550e8400-e29b-41d4-a716-446655440000-image.jpg",
  "s3Key": "fiscalizai-images/550e8400-e29b-41d4-a716-446655440000-image.jpg",
  "s3Url": "https://pub-xyz123.r2.dev/fiscalizai-images/550e8400-e29b-41d4-a716-446655440000-image.jpg"
}
```

**Campos de Resposta**:
- `id` - ID da imagem no banco de dados
- `name` - Nome único gerado para o arquivo (UUID + nome original)
- `s3Key` - Chave do objeto no bucket R2
- `s3Url` - URL pública para acessar a imagem

**Respostas de Erro**:

- `400 BAD REQUEST` - Arquivo não fornecido
```json
{
  "message": "Arquivo não fornecido para upload"
}
```

- `500 INTERNAL SERVER ERROR` - Erro ao fazer upload
```json
{
  "message": "Erro ao fazer upload do arquivo: [detalhes do erro]"
}
```

**Observações**:
- O arquivo é armazenado com um UUID único para evitar conflitos de nomes
- A URL retornada (`s3Url`) pode ser usada diretamente no campo `photo` das ocorrências
- O tipo de conteúdo (Content-Type) do arquivo é preservado no R2
- Certifique-se de que o bucket R2 está configurado para acesso público se desejar que as URLs sejam acessíveis

---

## 2. Users (Usuários)

**Base Path**: `/user`

**Descrição**: Gerencia os usuários do sistema (cidadãos que reportam ocorrências). Inclui cadastro, atualização, exclusão e consulta de perfil.

**Tradução**: Usuários/Cidadãos cadastrados

### 2.1. Criar Usuário (Signup)

```
POST /user
```

**Descrição**: Cadastra um novo usuário no sistema. Não requer autenticação.

**Autenticação**: Não requerida (PermitAll)

**Body (JSON)**:
```json
{
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "phone": "+55 11 98765-4321",
  "password": "senha123"
}
```

**Campos Obrigatórios**:
- `email` - Email único do usuário
- `password` - Senha do usuário

**Resposta de Sucesso**: `201 CREATED`
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "phone": "+55 11 98765-4321"
}
```

**Observação**: A senha não é retornada na resposta por segurança. O objeto retornado é um UserDTO.

---

### 2.2. Buscar Perfil do Usuário Autenticado

```
GET /user/me
```

**Descrição**: Retorna os dados do usuário atualmente autenticado.

**Autenticação**: Requerida (Roles: USER ou ADMIN)

**Resposta de Sucesso**: `200 OK`
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "phone": "+55 11 98765-4321"
}
```

**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado

---

### 2.3. Atualizar Usuário

```
PUT /user/{id}
```

**Parâmetros**:
- `id` (path) - ID do usuário

**Descrição**: Atualiza os dados de um usuário. O usuário só pode atualizar seus próprios dados, exceto se for ADMIN.

**Autenticação**: Requerida (Roles: USER ou ADMIN)

**Regras de Autorização**:
- Usuários comuns só podem atualizar seus próprios dados
- ADMINs podem atualizar qualquer usuário

**Body (JSON)**:
```json
{
  "name": "João Silva Santos",
  "email": "joao.santos@example.com",
  "phone": "+55 11 91234-5678",
  "password": "novaSenha456"
}
```

**Resposta de Sucesso**: `201 CREATED`
```json
{
  "id": 1,
  "name": "João Silva Santos",
  "email": "joao.santos@example.com",
  "phone": "+55 11 91234-5678"
}
```

**Respostas de Erro**:
- `400 BAD REQUEST` - Usuário tentando atualizar outro usuário sem ser ADMIN
- `401 UNAUTHORIZED` - Usuário não autenticado
- `404 NOT FOUND` - Usuário não encontrado

---

### 2.4. Excluir Usuário

```
DELETE /user/{id}
```

**Parâmetros**:
- `id` (path) - ID do usuário

**Descrição**: Remove permanentemente um usuário do sistema. O usuário só pode excluir sua própria conta, exceto se for ADMIN.

**Autenticação**: Requerida (Roles: USER ou ADMIN)

**Regras de Autorização**:
- Usuários comuns só podem excluir sua própria conta
- ADMINs podem excluir qualquer usuário

**Resposta de Sucesso**: `204 NO CONTENT`

**Respostas de Erro**:
- `400 BAD REQUEST` - Usuário tentando excluir outro usuário sem ser ADMIN
- `401 UNAUTHORIZED` - Usuário não autenticado
- `404 NOT FOUND` - Usuário não encontrado

---

## 3. Categories (Categorias)

**Base Path**: `/categories`

**Descrição**: Gerencia as categorias de problemas urbanos. Permite listar, criar, atualizar e excluir categorias. Operações de modificação requerem permissão de ADMIN.

**Tradução**: Categorias de problemas urbanos

### 3.1. Listar Categorias

```
GET /categories
```

**Descrição**: Retorna todas as categorias disponíveis no sistema.

**Autenticação**: Não requerida (PermitAll)

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Infraestrutura",
    "icon": "construction",
    "description": "Problemas relacionados à infraestrutura urbana",
    "tags": ["buraco", "calçada", "asfalto"]
  },
  {
    "id": 2,
    "name": "Iluminação Pública",
    "icon": "lightbulb",
    "description": "Problemas com iluminação de ruas e espaços públicos",
    "tags": ["poste", "lâmpada"]
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`
```json
{
  "message": "Nenhuma categoria encontrada"
}
```

---

### 3.2. Buscar Categoria por ID

```
GET /categories/{id}
```

**Parâmetros**:
- `id` (path) - ID da categoria

**Descrição**: Retorna os detalhes de uma categoria específica.

**Autenticação**: Não requerida (PermitAll)

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 3.3. Criar Nova Categoria

```
POST /categories
```

**Descrição**: Cria uma nova categoria no sistema.

**Autenticação**: Requerida (Role: ADMIN)

**Body (JSON)**:
```json
{
  "name": "Saneamento",
  "icon": "water_drop",
  "description": "Problemas relacionados a água e esgoto",
  "tags": ["esgoto", "água", "vazamento"]
}
```

**Campos Obrigatórios**:
- `name` - Nome da categoria

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN

---

### 3.4. Atualizar Categoria

```
PUT /categories/{id}
```

**Parâmetros**:
- `id` (path) - ID da categoria

**Descrição**: Atualiza os dados de uma categoria existente.

**Autenticação**: Requerida (Role: ADMIN)

**Body (JSON)**: Mesma estrutura do POST

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN
- `404 NOT FOUND` - Categoria não encontrada

---

### 3.5. Excluir Categoria

```
DELETE /categories/{id}
```

**Parâmetros**:
- `id` (path) - ID da categoria

**Descrição**: Remove permanentemente uma categoria do sistema.

**Autenticação**: Requerida (Role: ADMIN)

**Resposta de Sucesso**: `204 NO CONTENT`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN
- `404 NOT FOUND` - Categoria não encontrada

---

## 4. Status

**Base Path**: `/status`

**Descrição**: Gerencia os status possíveis para as ocorrências. Permite listar, criar, atualizar e excluir status. Operações de modificação requerem permissão de ADMIN.

**Tradução**: Status/Estado da ocorrência

### 4.1. Listar Status

```
GET /status
```

**Descrição**: Retorna todos os status disponíveis no sistema.

**Autenticação**: Não requerida (PermitAll)

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Pendente",
    "icon": "schedule"
  },
  {
    "id": 2,
    "name": "Em Análise",
    "icon": "search"
  },
  {
    "id": 3,
    "name": "Resolvida",
    "icon": "check_circle"
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`
```json
{
  "message": "Nenhum status encontrado"
}
```

---

### 4.2. Buscar Status por ID

```
GET /status/{id}
```

**Parâmetros**:
- `id` (path) - ID do status

**Descrição**: Retorna os detalhes de um status específico.

**Autenticação**: Não requerida (PermitAll)

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 4.3. Criar Novo Status

```
POST /status
```

**Descrição**: Cria um novo status no sistema.

**Autenticação**: Requerida (Role: ADMIN)

**Body (JSON)**:
```json
{
  "name": "Cancelada",
  "icon": "cancel"
}
```

**Campos Obrigatórios**:
- `name` - Nome do status

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN

---

### 4.4. Atualizar Status

```
PUT /status/{id}
```

**Parâmetros**:
- `id` (path) - ID do status

**Descrição**: Atualiza os dados de um status existente.

**Autenticação**: Requerida (Role: ADMIN)

**Body (JSON)**: Mesma estrutura do POST

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN
- `404 NOT FOUND` - Status não encontrado

---

### 4.5. Excluir Status

```
DELETE /status/{id}
```

**Parâmetros**:
- `id` (path) - ID do status

**Descrição**: Remove permanentemente um status do sistema.

**Autenticação**: Requerida (Role: ADMIN)

**Resposta de Sucesso**: `204 NO CONTENT`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN
- `404 NOT FOUND` - Status não encontrado

---

## 5. Severity (Severidade)

**Base Path**: `/severity`

**Descrição**: Gerencia os níveis de severidade das ocorrências. Permite listar, criar, atualizar e excluir severidades. Operações de modificação requerem permissão de ADMIN.

**Tradução**: Severidade/Gravidade da ocorrência

### 5.1. Listar Severidades

```
GET /severity
```

**Descrição**: Retorna todos os níveis de severidade disponíveis.

**Autenticação**: Não requerida (PermitAll)

**Resposta de Sucesso**: `200 OK`
```json
[
  {
    "id": 1,
    "name": "Baixa",
    "icon": "info"
  },
  {
    "id": 2,
    "name": "Média",
    "icon": "warning"
  },
  {
    "id": 3,
    "name": "Alta",
    "icon": "error"
  },
  {
    "id": 4,
    "name": "Crítica",
    "icon": "dangerous"
  }
]
```

**Resposta de Erro**: `404 NOT FOUND`
```json
{
  "message": "Nenhuma severidade encontrada"
}
```

---

### 5.2. Buscar Severidade por ID

```
GET /severity/{id}
```

**Parâmetros**:
- `id` (path) - ID da severidade

**Descrição**: Retorna os detalhes de uma severidade específica.

**Autenticação**: Não requerida (PermitAll)

**Resposta de Sucesso**: `200 OK`
**Resposta de Erro**: `404 NOT FOUND`

---

### 5.3. Criar Nova Severidade

```
POST /severity
```

**Descrição**: Cria uma nova severidade no sistema.

**Autenticação**: Requerida (Role: ADMIN)

**Body (JSON)**:
```json
{
  "name": "Urgente",
  "icon": "priority_high"
}
```

**Campos Obrigatórios**:
- `name` - Nome da severidade

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN

---

### 5.4. Atualizar Severidade

```
PUT /severity/{id}
```

**Parâmetros**:
- `id` (path) - ID da severidade

**Descrição**: Atualiza os dados de uma severidade existente.

**Autenticação**: Requerida (Role: ADMIN)

**Body (JSON)**: Mesma estrutura do POST

**Resposta de Sucesso**: `201 CREATED`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN
- `404 NOT FOUND` - Severidade não encontrada

---

### 5.5. Excluir Severidade

```
DELETE /severity/{id}
```

**Parâmetros**:
- `id` (path) - ID da severidade

**Descrição**: Remove permanentemente uma severidade do sistema.

**Autenticação**: Requerida (Role: ADMIN)

**Resposta de Sucesso**: `204 NO CONTENT`
**Respostas de Erro**:
- `401 UNAUTHORIZED` - Usuário não autenticado
- `403 FORBIDDEN` - Usuário não possui role ADMIN
- `404 NOT FOUND` - Severidade não encontrada

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

### Autenticação e Autorização

A API utiliza autenticação baseada em roles (RBAC - Role-Based Access Control) com dois níveis de permissão:

**Roles Disponíveis**:
- `USER` - Usuário comum que pode reportar e gerenciar suas próprias ocorrências
- `ADMIN` - Administrador com permissões completas no sistema

**Endpoints Públicos** (não requerem autenticação):
- `GET /categories` - Listar categorias
- `GET /categories/{id}` - Buscar categoria por ID
- `GET /severity` - Listar severidades
- `GET /severity/{id}` - Buscar severidade por ID
- `GET /status` - Listar status
- `GET /status/{id}` - Buscar status por ID
- `GET /location/cep/{cep}` - Buscar endereço por CEP
- `POST /user` - Criar novo usuário (signup)

**Endpoints que Requerem Autenticação USER ou ADMIN**:
- `GET /user/me` - Buscar perfil próprio
- `PUT /user/{id}` - Atualizar usuário (próprio ou qualquer se ADMIN)
- `DELETE /user/{id}` - Excluir usuário (próprio ou qualquer se ADMIN)
- Todos os endpoints de `/issues` (criar, atualizar, excluir ocorrências)

**Endpoints Exclusivos para ADMIN**:
- `POST /categories` - Criar categoria
- `PUT /categories/{id}` - Atualizar categoria
- `DELETE /categories/{id}` - Excluir categoria
- `POST /severity` - Criar severidade
- `PUT /severity/{id}` - Atualizar severidade
- `DELETE /severity/{id}` - Excluir severidade
- `POST /status` - Criar status
- `PUT /status/{id}` - Atualizar status
- `DELETE /status/{id}` - Excluir status

**Códigos de Resposta de Autenticação**:
- `401 UNAUTHORIZED` - Usuário não autenticado (sem token ou token inválido)
- `403 FORBIDDEN` - Usuário autenticado mas sem permissão para a operação (role insuficiente)

**Observação**: A implementação da autenticação utiliza o SecurityContext do Jakarta EE para gerenciar sessões e verificar roles

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
