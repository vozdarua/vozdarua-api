package io.fiscalizai.rest;

import io.fiscalizai.config.RequestLocale;
import io.fiscalizai.model.dto.ErrorResource;
import io.fiscalizai.model.entity.Severity;
import io.fiscalizai.model.messages.AppMessages;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/severity")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeverityResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    public Response list() {
        List<Severity> statuses = Severity.listAll();

        if(statuses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.severities_not_found())).build();
        }

        return Response.ok(statuses).build();
    }

}
