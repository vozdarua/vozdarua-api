package io.vozdarua.rest;

import io.vozdarua.controller.service.AccountService;
import io.vozdarua.model.dto.UserDTO;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    AccountService accountService;

    @POST
    @PermitAll
    @Transactional
    public Response create(@Valid User user) {
        accountService.signupUser(user);
        return Response.status(Response.Status.CREATED).entity(toUserDTO(user)).build();
    }

    @GET
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    @Path("/me")
    public Response userResource(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = User.find("email", email).singleResult();
        return Response.ok().entity(toUserDTO(user)).build();
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(user.id, user.phone, user.email, user.role);
    }


}
