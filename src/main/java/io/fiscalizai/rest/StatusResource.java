package io.fiscalizai.rest;

import io.fiscalizai.config.RequestLocale;
import io.fiscalizai.model.dto.ErrorResource;
import io.fiscalizai.model.entity.Status;
import io.fiscalizai.model.messages.AppMessages;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatusResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    public Response list() {
        List<Status> statuses = Status.listAll();

        if(statuses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.statuses_not_found())).build();
        }

        return Response.ok(statuses).build();
    }
}
