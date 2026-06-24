package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.ErrorResource;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.Severity;
import io.vozdarua.model.entity.Status;
import io.vozdarua.model.messages.AppMessages;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Objects;


@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatusResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @PermitAll
    public Response list() {
        List<Status> statuses = Status.listAll();
        if(statuses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.statuses_not_found())).build();
        }
        return Response.ok(statuses).build();
    }

    @POST
    @Transactional
    @RolesAllowed(Roles.ADMIN)
    public Response create(@Valid Status status) {
        status.persist();
        return Response.status(Response.Status.CREATED).entity(status).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed(Roles.ADMIN)
    public Response update(@PathParam("id") Long id, @Valid Status updatedStatus) {
        Status status = Status.findById(id);

        if(Objects.isNull(status)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.status_not_found())).build();
        }

        status.name = updatedStatus.name;
        status.icon = updatedStatus.icon;
        status.persist();

        return Response.status(Response.Status.CREATED).entity(status).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed(Roles.ADMIN)
    public Response delete(@PathParam("id") Long id) {
        Status status = Status.findById(id);
        if(Objects.isNull(status)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.status_not_found())).build();
        }
        status.delete();
        return  Response.noContent().build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Status status = Status.findById(id);
        if (status == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.status_not_found())).build();
        }
        return Response.ok(status).build();
    }
}
