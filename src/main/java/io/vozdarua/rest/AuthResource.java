package io.vozdarua.rest;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vozdarua.controller.service.AuthService;
import io.vozdarua.model.dto.AuthRequest;
import io.vozdarua.model.entity.User;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Objects;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService accountService;

    @POST
    @PermitAll
    @Path("/login")
    public Response login(AuthRequest request) {

        if (Objects.isNull(request) || Objects.isNull(request.email()) || request.email().isBlank() || Objects.isNull(request.password())
                || request.password().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User user = User.find("email", request.email()).firstResult();
        if (Objects.nonNull(user) && BcryptUtil.matches(request.password(), user.password)) {
            return Response.ok(accountService.token(user)).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
