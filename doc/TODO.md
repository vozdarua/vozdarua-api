# TODO — gaps de backend (auditoria frontend ↔ backend)

> Gerado em 07/09/2026 a partir de uma varredura completa do `fiscalizai-frontend`
> (todas as views, services e stores) cruzada com o inventário real da API atual
> (OpenAPI + código-fonte). Cada item abaixo tem a referência de onde no front
> a lacuna aparece hoje (mockado, client-side, ou placeholder explícito).

## 🔴 Alta prioridade (bloqueiam feature já visível no front)

- [ ] **Comentários em ocorrências.** Não existe entidade `Comment` nem endpoint
  algum. O front já chama isso em `MapDrawer.vue` via `services/comentarios.js`
  (`GET/POST /occurrences/{id}/comments`) e silencia o erro no catch
  ("API pode não ter o endpoint ainda"). Sugestão: `GET/POST /issues/{id}/comments`
  (usar `/issues`, não `/occurrences`, pra ficar consistente com o resto da API —
  o front vai precisar ajustar a URL quando isso existir).

- [ ] **"Minhas ocorrências" (self-scoped).** `MinhasView.vue` já documenta no
  próprio código (`MinhasView.vue:2-5`) que está esperando isso: hoje só existe
  `GET /issues/reporter/{id}`, restrito a `ADMIN`, e o campo `reporter` do
  `Issue` vem mascarado (`@SecureField(ADMIN)`) — um usuário comum nem consegue
  ver de quem é cada ocorrência. Precisa de um endpoint tipo `GET /issues/mine`
  que resolva o reporter a partir do JWT da própria requisição (sem precisar de
  `ADMIN` nem expor o `reporter` de outras ocorrências).

- [ ] **Feedback do app.** `FeedbackView.vue:13` só simula envio
  (`await new Promise(r => setTimeout(r, 900))`) — nada chega no backend. Precisa
  de um endpoint tipo `POST /feedback` (tipo, mensagem, nome/e-mail opcionais).

## 🟡 Média prioridade (funciona, mas é client-side e não escala)

- [ ] **Métricas por cidade.** `CityMetricsSheet.vue`/`CityMetricsSidebar.vue`
  calculam tudo no client (contagem por status/categoria/severidade/bairro) a
  partir da lista inteira de ocorrências da cidade. O `fiscalizai-briefing.md`
  original já previa um `GET /metricas?cidade=X&bairro=Y` com esse shape
  (emAberto, resolvidas, topBairros, topCategorias etc.) — nunca foi implementado.

- [ ] **Cidade como conceito real no backend.** Hoje `city` é só um campo texto
  livre em `Address`, sem normalização. O front usa uma lista **hardcoded** de
  ~65 cidades (`stores/cidade.js:15-86`) só pra alimentar o seletor de cidade.
  Um `GET /cities` (com contagem de ocorrências por cidade) deixaria isso
  consultável de verdade e mataria a lista hardcoded no front.

- [ ] **Paginação e filtros combináveis em `/issues`.** `GET /issues` carrega a
  tabela inteira sem paginação (`Issue.listAll()`), e os filtros
  (`/category/{id}`, `/status/{id}`, `/severity/{id}`, `/address`) são endpoints
  separados que não combinam entre si (ex: não dá pra filtrar categoria + cidade
  numa única chamada). Vira gargalo assim que o volume de ocorrências crescer.

- [ ] **Ranking por cidade.** `GET /issues/ranking` hoje só retorna top-5
  usuários por contagem, sem filtro de cidade. `RankingView.vue` calcula
  "top cidades" no client (`RankingView.vue:12-55`) por falta de um endpoint
  equivalente no backend.

## 🟢 Baixa prioridade / opcional

- [ ] **Alertas/notificações.** Feature inteira em standby —
  `AlertasView.vue:2-3` já é um placeholder explícito no código
  ("A API atual não expõe nenhum endpoint de alertas"). Não existe entidade,
  endpoint nem definição de webhook/polling. Antes de implementar vale decidir
  o design (push? polling? e-mail?), já que é a maior peça em aberto.

- [ ] **Refresh token (`POST /auth/refresh`).** Hoje o JWT expira e o usuário é
  deslogado direto no 401 (`services/api.js:17-21`). Funciona, mas é uma UX
  pior que renovar silenciosamente. Baixo risco de deixar pra depois.

- [ ] **Sistema de pontos/gamificação.** Mencionado no `fiscalizai-briefing.md`
  antigo ("+1 ponto por ocorrência registrada"), nunca implementado — o ranking
  hoje é contagem bruta de ocorrências, não pontos. Esse briefing é de uma
  versão anterior do projeto (descrevia migração pra React; o projeto seguiu em
  Vue), então vale confirmar se esse recurso ainda é desejado antes de investir
  nele.

## Já implementado e funcionando (confirmado na auditoria)

- Auth completo: login, cadastro, `GET /user/me`, editar usuário, **esqueci/redefinir
  senha** (`POST /auth/password/recovery` + `PUT /auth/password/reset`).
- CRUD de Issues: criar, listar, buscar por id/categoria/status/severidade/endereço,
  confirmar, resolver, upload de imagem (`POST /issues/image/upload` via Cloudflare R2).
- CRUD de Categories/Status/Severity (admin).
- Consulta de CEP (`GET /location/cep/{cep}`) e geocodificação, proxied pelo backend.

## Notas soltas (fora do escopo de backend, registradas pra não perder)

- `StepLocalizacao.vue:382` referencia `@change="refinarPorNumero"`, uma função
  que não existe no `<script setup>` do componente — bug de frontend, handler morto.
- Código morto no front que pode ser limpo quando alguém for mexer nessas áreas:
  `services/metadata.js` (nunca importado), `components/map/CityMetrics.vue`,
  `components/modals/CategoryModal.vue`, `components/occurrence/OccurrenceCard.vue`
  (não usados em lugar nenhum), `services/auth.js:excluirUsuario` e alguns exports
  de `services/ocorrencias.js` sem chamador.
