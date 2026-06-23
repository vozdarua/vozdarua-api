package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.ErrorResource;
import io.vozdarua.model.entity.Category;
import io.vozdarua.model.messages.AppMessages;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriesResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    public Response list() {
        List<PanacheEntityBase> categories = Category.listAll();

        if(categories.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.categories_not_found())).build();
        }

        return Response.ok(categories).build();
    }
}
