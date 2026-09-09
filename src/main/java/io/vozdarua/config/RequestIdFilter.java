package io.vozdarua.config;

import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

import java.util.Objects;
import java.util.UUID;

// Põe um requestId (e userId quando autenticado) no MDC de cada request, pra correlacionar
// as linhas de log de uma mesma requisição no stdout do Railway quando várias chegam juntas.
// MDC é limpo na resposta porque Quarkus reusa threads de worker entre requests.
// Sem @Priority: fica na prioridade default (USER, 5000), depois da autenticação (1000) —
// precisa disso pra getUserPrincipal() já vir preenchido quando autenticado.
@Provider
public class RequestIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        if (Objects.nonNull(requestContext.getSecurityContext())
                && Objects.nonNull(requestContext.getSecurityContext().getUserPrincipal())) {
            // O JWT subject aqui é o email do usuário - mascarado porque o MDC vai pra
            // toda linha de log de request autenticado (stdout do Railway, terceiro).
            MDC.put(USER_ID, VozDaRuaUtils.maskEmail(requestContext.getSecurityContext().getUserPrincipal().getName()));
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        MDC.clear();
    }
}
