# Guia de Início Rápido

Este guia ajudará você a começar a usar a API Voz da Rua rapidamente.

## Pré-requisitos

- Java 21+
- PostgreSQL 12+
- Maven 3.9+
- Docker (opcional)

## Instalação e Configuração

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/vozdarua-api.git
cd vozdarua-api
```

### 2. Configure o Banco de Dados

#### Opção A: PostgreSQL Local

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE vozdarua_db;
CREATE USER postgres WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE vozdarua_db TO postgres;
```

#### Opção B: Docker

```bash
docker run --name vozdarua-postgres \
  -e POSTGRES_DB=vozdarua_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=secret \
  -p 5432:5432 \
  -d postgres:16
```

### 3. Execute a Aplicação

#### Modo Desenvolvimento

```bash
./mvnw quarkus:dev
```

A aplicação estará disponível em: `http://localhost:8080`

#### Modo Produção

```bash
# Compilar
./mvnw package

# Executar
java -jar target/quarkus-app/quarkus-run.jar
```

### 4. Acesse o Swagger UI

Abra no navegador: http://localhost:8080/q/swagger-ui

---

## Primeiros Passos

### 1. Criar um Usuário

**Requisição**:
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao.silva@example.com",
    "phone": "+55 11 98765-4321"
  }'
```

**Resposta**:
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "phone": "+55 11 98765-4321"
}
```

### 2. Listar Categorias Disponíveis

**Requisição**:
```bash
curl -X GET http://localhost:8080/categories
```

**Resposta**:
```json
[
  {
    "id": 1,
    "name": "Infraestrutura"
  },
  {
    "id": 2,
    "name": "Iluminação Pública"
  }
]
```

### 3. Listar Status Disponíveis

**Requisição**:
```bash
curl -X GET http://localhost:8080/status
```

### 4. Listar Severidades Disponíveis

**Requisição**:
```bash
curl -X GET http://localhost:8080/severity
```

### 5. Criar uma Ocorrência

**Requisição**:
```bash
curl -X POST http://localhost:8080/issues \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Buraco grande na pista causando risco de acidentes",
    "address": "Avenida Paulista, 1578 - São Paulo",
    "reporter": {
      "id": 1
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
  }'
```

**Resposta**:
```json
{
  "id": 1,
  "description": "Buraco grande na pista causando risco de acidentes",
  "confirmIssue": 0,
  "address": "Avenida Paulista, 1578 - São Paulo",
  "reporter": { ... },
  "category": { ... },
  "severity": { ... },
  "status": { ... }
}
```

### 6. Listar Todas as Ocorrências

**Requisição**:
```bash
curl -X GET http://localhost:8080/issues
```

### 7. Confirmar uma Ocorrência

Quando outro cidadão quer confirmar que o problema existe:

**Requisição**:
```bash
curl -X PUT http://localhost:8080/issues/confirm/1
```

**Resposta**:
```json
{
  "id": 1,
  "confirmIssue": 1,
  ...
}
```

### 8. Validar um CEP

**Requisição**:
```bash
curl -X GET http://localhost:8080/location/cep/01310-100
```

**Resposta**:
```json
{
  "cep": "01310-100",
  "state": "SP",
  "city": "São Paulo",
  "neighborhood": "Bela Vista",
  "street": "Avenida Paulista"
}
```

---

## Fluxo de Trabalho Típico

### Cenário: Cidadão Reporta Buraco na Rua

1. **Cidadão se cadastra** → `POST /user`
2. **Cidadão consulta categorias** → `GET /categories`
3. **Cidadão valida o CEP do local** → `GET /location/cep/{cep}`
4. **Cidadão cria a ocorrência** → `POST /issues`
5. **Outros cidadãos confirmam a ocorrência** → `PUT /issues/confirm/{id}`
6. **Prefeitura atualiza o status** → `PUT /issues/{id}`
7. **Problema é resolvido** → `PUT /issues/{id}` (status = "Resolvida")

---

## Testando com Swagger UI

1. Acesse: http://localhost:8080/q/swagger-ui
2. Expanda o endpoint desejado
3. Clique em "Try it out"
4. Preencha os parâmetros
5. Clique em "Execute"
6. Veja a resposta abaixo

### Vantagens do Swagger UI

- Interface visual intuitiva
- Não precisa escrever comandos curl
- Valida dados automaticamente
- Mostra exemplos de requisição/resposta
- Permite testar autenticação
- Exporta requisições para várias linguagens

---

## Internacionalização

A API suporta múltiplos idiomas. Use o header `Accept-Language`:

### Português (padrão)
```bash
curl -X GET http://localhost:8080/issues/999 \
  -H "Accept-Language: pt-BR"
```

**Resposta**:
```json
{
  "message": "Ocorrência não encontrada"
}
```

### Inglês
```bash
curl -X GET http://localhost:8080/issues/999 \
  -H "Accept-Language: en"
```

**Resposta**:
```json
{
  "message": "Issue not found"
}
```

---

## Dados Iniciais

O arquivo `src/main/resources/import.sql` contém dados iniciais que são carregados automaticamente:

- Categorias pré-cadastradas
- Status pré-cadastrados
- Severidades pré-cadastradas
- Alguns usuários e ocorrências de exemplo

### Ver Dados Iniciais

Verifique o arquivo para entender quais dados estão pré-carregados:

```bash
cat src/main/resources/import.sql
```

---

## Variáveis de Ambiente (Produção)

Para deploy em produção, configure:

```bash
export DB_HOST=seu-host-postgres.com
export DB_PORT=5432
export DB_NAME=vozdarua_db
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha_segura
export PORT=8080
```

Ou crie um arquivo `.env`:

```
DB_HOST=seu-host-postgres.com
DB_PORT=5432
DB_NAME=vozdarua_db
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha_segura
PORT=8080
```

---

## Troubleshooting

### Erro: "Database connection failed"

Verifique se:
1. PostgreSQL está rodando
2. Credenciais em `application.properties` estão corretas
3. Banco `vozdarua_db` foi criado

### Erro: "Port 8080 already in use"

Mude a porta:
```bash
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

### Swagger UI não carrega

Verifique se `quarkus-smallrye-openapi` está no `pom.xml` e se você está em modo dev ou se configurou:
```properties
quarkus.swagger-ui.always-include=true
```

### Mensagens em inglês mesmo com pt-BR

Verifique o header `Accept-Language` nas requisições.

---

## Próximos Passos

1. Explore a [documentação completa dos endpoints](./API_ENDPOINTS.md)
2. Consulte os [modelos de dados](./DATA_MODELS.md)
3. Veja o [glossário de termos](./GLOSSARY.md)
4. Implemente autenticação JWT
5. Adicione upload real de imagens
6. Integre com mapas (Google Maps, OpenStreetMap)
7. Crie notificações push para atualizações de status

---

## Recursos Adicionais

- [Documentação do Quarkus](https://quarkus.io/guides/)
- [Hibernate Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [OpenAPI/Swagger](https://quarkus.io/guides/openapi-swaggerui)
- [REST Client](https://quarkus.io/guides/rest-client-reactive)

---

## Suporte

Para dúvidas ou problemas:
- Abra uma issue no GitHub
- Consulte a documentação completa em `/doc`
- Verifique os logs da aplicação
