# Modelos de Dados

Esta documentação descreve as entidades e estruturas de dados utilizadas pela API Voz da Rua.

## Índice

1. [Issue (Ocorrência)](#issue-ocorrência)
2. [FiscalizaiUser (Usuário)](#user-usuário)
3. [Category (Categoria)](#category-categoria)
4. [Status](#status)
5. [Severity (Severidade)](#severity-severidade)
6. [Location (Dados de Localização)](#location-dados-de-localização)
7. [ErrorResource (Mensagem de Erro)](#errorresource-mensagem-de-erro)

---

## Issue (Ocorrência)

**Descrição**: Representa uma ocorrência de problema urbano reportada por um cidadão.

**Tradução**: Ocorrência/Reporte de problema urbano

### Estrutura

```json
{
  "id": 1,
  "description": "Buraco grande na pista principal",
  "confirmIssue": 8,
  "photo": "https://storage.example.com/photos/issue-001.jpg",
  "address": "Avenida Paulista, 1578 - Bela Vista, São Paulo - SP",
  "reporter": {
    "id": 1,
    "name": "João Silva",
    "email": "joao.silva@example.com",
    "phone": "+55 11 98765-4321"
  },
  "category": {
    "id": 1,
    "name": "Infraestrutura"
  },
  "severity": {
    "id": 3,
    "name": "Alta"
  },
  "status": {
    "id": 1,
    "name": "Pendente"
  }
}
```

### Campos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | Long | Auto | Identificador único da ocorrência |
| `description` | String | Sim | Descrição detalhada do problema |
| `confirmIssue` | Integer | Não | Número de confirmações da ocorrência por outros usuários |
| `photo` | String | Não | URL ou Base64 da foto do problema |
| `address` | String | Não | Endereço completo do local |
| `reporter` | FiscalizaiUser | Sim | Usuário que reportou a ocorrência |
| `category` | Category | Não | Categoria do problema |
| `severity` | Severity | Não | Nível de gravidade |
| `status` | Status | Não | Status atual da ocorrência |

### Validações

- `description`: Não pode ser nulo ou vazio
- `reporter.id`: Deve referenciar um usuário existente
- `category.id`: Se fornecido, deve referenciar uma categoria existente
- `severity.id`: Se fornecido, deve referenciar uma severidade existente
- `status.id`: Se fornecido, deve referenciar um status existente

---

## FiscalizaiUser (Usuário)

**Descrição**: Representa um cidadão cadastrado no sistema que pode reportar ocorrências.

**Tradução**: Usuário/Cidadão

### Estrutura

```json
{
  "id": 1,
  "name": "Maria Santos",
  "email": "maria.santos@example.com",
  "phone": "+55 21 99876-5432"
}
```

### Campos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | Long | Auto | Identificador único do usuário |
| `name` | String | Sim | Nome completo do usuário |
| `email` | String | Sim | E-mail válido |
| `phone` | String | Não | Telefone de contato |

### Validações

- `name`: Não pode ser nulo ou vazio
- `email`: Deve ser um e-mail válido
- `email`: Deve ser único no sistema

---

## Category (Categoria)

**Descrição**: Classificação do tipo de problema urbano.

**Tradução**: Categoria de problema

### Estrutura

```json
{
  "id": 1,
  "name": "Infraestrutura",
  "description": "Problemas relacionados a ruas, calçadas, pontes e estruturas urbanas"
}
```

### Campos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | Long | Auto | Identificador único da categoria |
| `name` | String | Sim | Nome da categoria |
| `description` | String | Não | Descrição detalhada da categoria |

### Exemplos de Categorias

1. **Infraestrutura**: Buracos, calçadas quebradas, pontes danificadas
2. **Iluminação Pública**: Postes apagados, lâmpadas queimadas
3. **Limpeza Urbana**: Lixo acumulado, entulho, descarte irregular
4. **Sinalização**: Placas danificadas, semáforos defeituosos
5. **Segurança Pública**: Áreas perigosas, falta de policiamento
6. **Meio Ambiente**: Poluição, árvores caídas, áreas verdes abandonadas
7. **Saúde Pública**: Focos de dengue, água parada, pragas urbanas

---

## Status

**Descrição**: Estado atual de processamento de uma ocorrência.

**Tradução**: Status/Estado da ocorrência

### Estrutura

```json
{
  "id": 1,
  "name": "Pendente",
  "description": "Aguardando análise pela prefeitura"
}
```

### Campos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | Long | Auto | Identificador único do status |
| `name` | String | Sim | Nome do status |
| `description` | String | Não | Descrição do significado do status |

### Ciclo de Vida Típico

```
Pendente → Em Análise → Em Andamento → Resolvida → Fechada
                     ↓
                  Rejeitada
```

### Exemplos de Status

1. **Pendente**: Ocorrência recém-criada, aguardando triagem
2. **Em Análise**: Equipe municipal está avaliando a ocorrência
3. **Em Andamento**: Trabalhos de correção em execução
4. **Resolvida**: Problema foi corrigido
5. **Fechada**: Ocorrência arquivada
6. **Rejeitada**: Ocorrência considerada inválida ou duplicada

---

## Severity (Severidade)

**Descrição**: Nível de gravidade/urgência de uma ocorrência.

**Tradução**: Severidade/Gravidade

### Estrutura

```json
{
  "id": 3,
  "name": "Alta",
  "description": "Problema que requer atenção urgente",
  "priority": 3
}
```

### Campos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | Long | Auto | Identificador único da severidade |
| `name` | String | Sim | Nome do nível de severidade |
| `description` | String | Não | Descrição do nível |
| `priority` | Integer | Não | Ordem de prioridade (maior = mais urgente) |

### Níveis de Severidade

| Nível | Descrição | Exemplos | Tempo de Resposta Sugerido |
|-------|-----------|----------|---------------------------|
| **Baixa** | Problema estético ou de baixo impacto | Pintura desgastada, lixeira cheia | 30 dias |
| **Média** | Problema que causa incômodo moderado | Buraco pequeno, poste apagado | 15 dias |
| **Alta** | Problema que afeta segurança ou mobilidade | Buraco grande, semáforo quebrado | 7 dias |
| **Crítica** | Problema de risco iminente | Via interditada, risco de desabamento | 24 horas |

---

## Location (Dados de Localização)

**Descrição**: Informações de endereço obtidas a partir de CEP via integração com Brasil API.

**Tradução**: Localização/Dados de endereço

### Estrutura (Resposta da API)

```json
{
  "cep": "01310-100",
  "state": "SP",
  "city": "São Paulo",
  "neighborhood": "Bela Vista",
  "street": "Avenida Paulista",
  "service": "viacep",
  "location": {
    "type": "Point",
    "coordinates": {
      "longitude": "-46.6388",
      "latitude": "-23.5614"
    }
  }
}
```

### Campos

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `cep` | String | CEP formatado (00000-000) |
| `state` | String | Sigla do estado (UF) |
| `city` | String | Nome da cidade |
| `neighborhood` | String | Bairro |
| `street` | String | Nome da rua/avenida |
| `service` | String | Serviço usado para consulta (viacep, brasilapi, etc.) |
| `location` | Object | Coordenadas geográficas (quando disponível) |

### Uso

Este modelo é retornado pelo endpoint `/location/cep/{cep}` e pode ser usado para:
- Validar CEPs informados pelos usuários
- Auto-completar endereços
- Obter coordenadas geográficas para mapas

---

## ErrorResource (Mensagem de Erro)

**Descrição**: Estrutura padrão de resposta de erro da API.

**Tradução**: Recurso de erro/Mensagem de erro

### Estrutura

```json
{
  "message": "Ocorrência não encontrada"
}
```

### Campos

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `message` | String | Mensagem de erro em linguagem natural |

### Exemplos de Mensagens de Erro

#### Português (pt-BR)
```json
{
  "message": "Ocorrência não encontrada"
}
```

#### Inglês (en)
```json
{
  "message": "Issue not found"
}
```

### Códigos HTTP Comuns

| Código | Significado | Quando Ocorre |
|--------|-------------|---------------|
| `200` | OK | Requisição bem-sucedida |
| `201` | Created | Recurso criado com sucesso |
| `204` | No Content | Exclusão bem-sucedida |
| `400` | Bad Request | Dados inválidos na requisição |
| `404` | Not Found | Recurso não encontrado |
| `500` | Internal Server Error | Erro interno do servidor |
| `502` | Bad Gateway | Serviço externo indisponível |

---

## Relacionamentos entre Entidades

```
FiscalizaiUser (1) ─────< (N) Issue
                              │
                              ├──> (1) Category
                              ├──> (1) Status
                              └──> (1) Severity
```

### Descrição dos Relacionamentos

- **Um usuário pode reportar várias ocorrências** (1:N)
- **Uma ocorrência pertence a uma categoria** (N:1)
- **Uma ocorrência possui um status** (N:1)
- **Uma ocorrência possui uma severidade** (N:1)

---

## Exemplo Completo de Payload

### Criar uma Ocorrência Completa

```json
{
  "description": "Buraco grande na via, causando risco de acidentes",
  "photo": "https://storage.example.com/issue-photos/abc123.jpg",
  "address": "Avenida Paulista, 1578 - Bela Vista, São Paulo - SP, 01310-200",
  "reporter": {
    "id": 42
  },
  "category": {
    "id": 1
  },
  "severity": {
    "id": 3
  },
  "status": {
    "id": 1
  }
}
```

### Resposta da API

```json
{
  "id": 156,
  "description": "Buraco grande na via, causando risco de acidentes",
  "confirmIssue": 0,
  "photo": "https://storage.example.com/issue-photos/abc123.jpg",
  "address": "Avenida Paulista, 1578 - Bela Vista, São Paulo - SP, 01310-200",
  "reporter": {
    "id": 42,
    "name": "Carlos Mendes",
    "email": "carlos.mendes@example.com",
    "phone": "+55 11 91234-5678"
  },
  "category": {
    "id": 1,
    "name": "Infraestrutura"
  },
  "severity": {
    "id": 3,
    "name": "Alta"
  },
  "status": {
    "id": 1,
    "name": "Pendente"
  }
}
```
