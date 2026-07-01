package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.Category;
import io.vozdarua.model.entity.Roles;
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

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriesResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @PermitAll
    public Response list() {
        List<Category> categories = Category.listAll();
        if(categories.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.categories_not_found())).build();
        }
        return Response.ok(categories).build();
    }

    @POST
    @Transactional
    @RolesAllowed(Roles.ADMIN)
    public Response create(@Valid Category category) {
        category.persist();
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed(Roles.ADMIN)
    public Response update(@PathParam("id") Long id, @Valid Category updatedCcategory) {
        Category category = Category.findById(id);

        if(Objects.isNull(category)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.categories_not_found())).build();
        }

        category.name = updatedCcategory.name;
        category.icon = updatedCcategory.icon;
        category.description = updatedCcategory.description;
        category.tags = updatedCcategory.tags;
        category.persist();

        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed(Roles.ADMIN)
    public Response delete(@PathParam("id") Long id) {
        Category category = Category.findById(id);
        if(Objects.isNull(category)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.categories_not_found())).build();
        }
        category.delete();
        return  Response.noContent().build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Category category = Category.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.categories_not_found())).build();
        }
        return Response.ok(category).build();
    }
}
