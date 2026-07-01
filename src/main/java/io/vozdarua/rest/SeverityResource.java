package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.Severity;
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

@Path("/severity")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeverityResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @PermitAll
    public Response list() {
        List<Severity> statuses = Severity.listAll();
        if(statuses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.severities_not_found())).build();
        }
        return Response.ok(statuses).build();
    }

    @POST
    @Transactional
    @RolesAllowed(Roles.ADMIN)
    public Response create(@Valid Severity severity) {
        severity.persist();
        return Response.status(Response.Status.CREATED).entity(severity).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed(Roles.ADMIN)
    public Response update(@PathParam("id") Long id, @Valid Severity updatedSeverity) {
        Severity severity = Severity.findById(id);

        if(Objects.isNull(severity)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.severity_not_found())).build();
        }

        severity.name = updatedSeverity.name;
        severity.icon = updatedSeverity.icon;
        severity.persist();

        return Response.status(Response.Status.CREATED).entity(severity).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed(Roles.ADMIN)
    public Response delete(@PathParam("id") Long id) {
        Severity severity = Severity.findById(id);
        if(Objects.isNull(severity)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.severity_not_found())).build();
        }
        severity.delete();
        return  Response.noContent().build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Severity severity = Severity.findById(id);
        if (severity == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.severity_not_found())).build();
        }
        return Response.ok(severity).build();
    }

}
