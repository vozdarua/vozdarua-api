# Glossário - Termos Técnicos (Inglês → Português)

Este glossário apresenta a tradução dos termos utilizados na API e no código-fonte do Voz da Rua.

## Termos Principais

| Termo em Inglês | Termo em Português | Descrição |
|-----------------|-------------------|-----------|
| **Issue** | **Ocorrência** | Problema urbano reportado por um cidadão (ex: buraco na rua, iluminação quebrada) |
| **Reporter** | **Denunciante/Relator** | Usuário que reportou a ocorrência |
| **User** | **Usuário** | Cidadão cadastrado no sistema |
| **Category** | **Categoria** | Classificação do tipo de problema (ex: Infraestrutura, Limpeza Urbana) |
| **Severity** | **Severidade/Gravidade** | Nível de gravidade da ocorrência (ex: Baixa, Média, Alta, Crítica) |
| **Status** | **Status/Estado** | Estado atual da ocorrência (ex: Pendente, Em Análise, Resolvida) |
| **Location** | **Localização** | Dados de localização geográfica da ocorrência |
| **CEP** | **CEP** | Código de Endereçamento Postal (Brasil) |
| **Address** | **Endereço** | Endereço completo do local da ocorrência |
| **Photo** | **Foto** | Evidência fotográfica da ocorrência |
| **Confirm Issue** | **Confirmar Ocorrência** | Ação de validar/confirmar uma ocorrência reportada |
| **Description** | **Descrição** | Detalhamento textual da ocorrência |
| **Tag** | **Etiqueta/Marcador** | Marcadores para facilitar buscas e categorização |

## Endpoints e Recursos

| Termo em Inglês | Termo em Português | Descrição |
|-----------------|-------------------|-----------|
| `/issues` | `/ocorrencias` (conceitual) | Endpoint de ocorrências |
| `/user` | `/usuario` (conceitual) | Endpoint de usuários |
| `/categories` | `/categorias` (conceitual) | Endpoint de categorias |
| `/status` | `/status` (conceitual) | Endpoint de status |
| `/severity` | `/severidade` (conceitual) | Endpoint de severidades |
| `/location` | `/localizacao` (conceitual) | Endpoint de localização |

**Nota**: Os endpoints na API mantêm nomenclatura em inglês seguindo padrões REST.

## Operações HTTP (CRUD)

| Método HTTP | Operação | Descrição |
|-------------|----------|-----------|
| **GET** | Listar/Obter | Recuperar dados |
| **POST** | Criar | Criar novo registro |
| **PUT** | Atualizar | Atualizar registro existente |
| **DELETE** | Excluir | Remover registro |

## Campos e Atributos Comuns

| Campo (Inglês) | Campo (Português) | Descrição |
|----------------|------------------|-----------|
| `id` | `identificador` | Identificador único do registro |
| `createdAt` | `criadoEm` | Data/hora de criação |
| `updatedAt` | `atualizadoEm` | Data/hora da última atualização |
| `confirmIssue` | `confirmacoesOcorrencia` | Contador de confirmações da ocorrência |
| `confirmResolve` | `confirmacoesResolucao` | Contador de confirmações de resolução da ocorrência |

## Status da Ocorrência (Exemplos)

| Status (Inglês) | Status (Português) |
|-----------------|-------------------|
| Pending | Pendente |
| In Analysis | Em Análise |
| In Progress | Em Andamento |
| Resolved | Resolvida |
| Closed | Fechada |
| Rejected | Rejeitada |

## Níveis de Severidade (Exemplos)

| Severity (Inglês) | Severidade (Português) |
|-------------------|----------------------|
| Low | Baixa |
| Medium | Média |
| High | Alta |
| Critical | Crítica |

## Categorias de Ocorrências (Exemplos)

| Category (Inglês) | Categoria (Português) |
|-------------------|---------------------|
| Infrastructure | Infraestrutura |
| Urban Cleaning | Limpeza Urbana |
| Public Lighting | Iluminação Pública |
| Traffic Signs | Sinalização de Trânsito |
| Public Safety | Segurança Pública |
| Environment | Meio Ambiente |
| Public Health | Saúde Pública |

## Mensagens de Erro Comuns

| Mensagem (Inglês) | Mensagem (Português) |
|-------------------|---------------------|
| Issue not found | Ocorrência não encontrada |
| User not found | Usuário não encontrado |
| Category not found | Categoria não encontrada |
| Invalid CEP | CEP inválido |
| Required field | Campo obrigatório |
| Validation error | Erro de validação |

## Termos Técnicos do Framework

| Termo | Descrição |
|-------|-----------|
| **Panache** | Camada de abstração do Hibernate que simplifica operações de banco de dados |
| **REST Client** | Cliente HTTP para consumir APIs externas |
| **DTO** | Data Transfer Object - Objeto de transferência de dados |
| **Entity** | Entidade JPA mapeada para tabela do banco de dados |
| **Resource** | Classe que expõe endpoints REST |
| **Repository** | Padrão de acesso a dados (implícito no Panache) |
