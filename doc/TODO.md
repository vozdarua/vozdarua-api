# TODO — gaps de backend (auditoria frontend ↔ backend)

> Gerado em 07/09/2026 a partir de uma varredura completa do `fiscalizai-frontend`
> (todas as views, services e stores) cruzada com o inventário real da API atual
> (OpenAPI + código-fonte). Cada item abaixo tem a referência de onde no front
> a lacuna aparece hoje (mockado, client-side, ou placeholder explícito).

## 🔴 Alta prioridade (bloqueiam feature já visível no front)

- [x] **Comentários em ocorrências.** Não existe entidade `Comment` nem endpoint
  algum. O front já chama isso em `MapDrawer.vue` via `services/comentarios.js`
  (`GET/POST /occurrences/{id}/comments`) e silencia o erro no catch
  ("API pode não ter o endpoint ainda"). Sugestão: `GET/POST /issues/{id}/comments`
  (usar `/issues`, não `/occurrences`, pra ficar consistente com o resto da API —
  o front vai precisar ajustar a URL quando isso existir).

- [x] **"Minhas ocorrências" (self-scoped).** `MinhasView.vue` já documenta no
  próprio código (`MinhasView.vue:2-5`) que está esperando isso: hoje só existe
  `GET /issues/reporter/{id}`, restrito a `ADMIN`, e o campo `reporter` do
  `Issue` vem mascarado (`@SecureField(ADMIN)`) — um usuário comum nem consegue
  ver de quem é cada ocorrência. Precisa de um endpoint tipo `GET /issues/mine` em UserResource
  que resolva o reporter a partir do JWT da própria requisição (sem precisar de
  `ADMIN` nem expor o `reporter` de outras ocorrências).

- [x] **Feedback do app.** `FeedbackView.vue:13` só simula envio
  (`await new Promise(r => setTimeout(r, 900))`) — nada chega no backend. Precisa
  de um endpoint tipo `POST /feedback` (tipo, mensagem, nome/e-mail opcionais).

## 🟡 Média prioridade (funciona, mas é client-side e não escala)

- [x] **Métricas por cidade.** `GET /issues/metrics?cityId=&neighborhood=`
  agora agrega no backend (total, `byStatus`, `byCategory` top-6,
  `byNeighborhood` top-6, `bySeverity`), no mesmo estilo de `rankingByCity`.
  `CityMetricsSheet.vue`/`CityMetricsSidebar.vue` não recalculam mais isso a
  partir da lista inteira de ocorrências — as ~40 linhas de redução
  duplicadas nos dois viraram um composable único
  (`useCityMetricsBreakdown`).

- [x] **Cidade como conceito real no backend.** `City`/`State` agora são
  entidades reais (seed com todas as ~5571 cidades do IBGE), `Address` ganhou
  `cityRef`/`stateRef` (FK best-effort, resolvida a partir do texto livre
  `city`/`state` que já existia — nunca bloqueia a criação da ocorrência se
  não achar match). `GET /cities?search=` e `GET /cities/ranking` (contagem
  agregada) substituem a lista hardcoded do front, e
  `GET /location/cities/nearby?lat=&lng=` (via Geoapify) resolve "cidades
  próximas" pela localização real do usuário em vez de distância entre as
  ~65 cidades chumbadas.

- [x] **Paginação e filtros combináveis em `/issues`.** `GET /issues` agora
  pagina (`page`/`size`, resposta `{content, page, size, totalElements,
  totalPages}`) e aceita `cityId`/`stateId`/`neighborhood`/`categoryId`/
  `statusId`/`severityId` combináveis entre si numa única chamada. Os
  endpoints dedicados (`/category/{id}`, `/status/{id}`, `/severity/{id}`,
  `/address`) foram removidos — nenhum tinha consumidor no front. Sem match
  agora é `200` + `content: []`, não mais `404`/`400`.

- [x] **Ranking por cidade.** `GET /issues/ranking?cityId=` agora aceita um
  filtro opcional de cidade (mesmo padrão de `rankingByCity`), e passou a
  retornar também a contagem de resolvidas (`total`/`resolved`, igual ao
  `/cities/ranking`). `RankingView.vue` não calcula mais o ranking de
  contribuidores no client — o cálculo anterior usava `oc.user` (campo que
  não existe na API, é `reporter`), então nunca funcionava de verdade.

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
