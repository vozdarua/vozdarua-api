package io.vozdarua.rest;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.service.AuthService;
import io.vozdarua.controller.service.PasswordRecoveryService;
import io.vozdarua.model.dto.AuthRequest;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.dto.UserDTO;
import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.entity.PasswordResetToken;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.ratelimit.RateLimited;
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

import java.util.Objects;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    AuthService accountService;

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @POST
    @PermitAll
    @Transactional
    @RateLimited(limit = 5, windowSeconds = 3600)
    public Response create(@Valid User user) {

        if(User.findByEmailOrPhone(user.email, user.phone).isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(appMessages.user_exists()).build();
        }

        accountService.signupUser(user);
        return Response.status(Response.Status.CREATED).entity(accountService.token(user)).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @RolesAllowed({Roles.ADMIN, Roles.USER})
    public Response update(@PathParam("id") Long id, @Valid User updatedUser, @Context SecurityContext securityContext) {
        User user = User.findById(id);
        if(Objects.isNull(user)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.user_not_found())).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        boolean isAdmin = securityContext.isUserInRole(Roles.ADMIN);

        if(!user.email.equals(email) && !isAdmin) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        user.phone = updatedUser.phone;
        user.email = updatedUser.email;
        user.password = BcryptUtil.bcryptHash(updatedUser.password);

        user.persist();
        return Response.status(Response.Status.CREATED).entity(UserDTO.toUserDTO(user, Issue.count("reporter.email", user.email))).build();

    }

    @GET
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    @Path("/me")
    public Response me(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = User.find("email", email).singleResult();
        return Response.ok().entity(UserDTO.toUserDTO(user, Issue.count("reporter.email", user.email))).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed({Roles.ADMIN, Roles.USER})
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        User user = User.findById(id);

        if(Objects.isNull(user)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.user_not_found())).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        boolean isAdmin = securityContext.isUserInRole(Roles.ADMIN);

        if(!user.email.equals(email) && !isAdmin) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        user.delete();
        return  Response.noContent().build();
    }

}
