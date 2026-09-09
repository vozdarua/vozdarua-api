package io.vozdarua.config;

import io.vozdarua.model.dto.MessageResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

// ponytail: catches only what no resource-specific handler already caught (see LocationResource,
// IssueResource) — a last line of defense so an unhandled exception in prod always leaves a
// stack trace in the logs instead of vanishing, which is exactly what made the Railway lat/long
// and "próximos" issues impossible to diagnose before this.
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable e) {
        LOGGER.errorf(e, "Erro não tratado em %s", uriInfo.getPath());
        return Response.serverError().entity(new MessageResponse("Erro interno")).build();
    }
}
