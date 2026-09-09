package io.vozdarua.rest;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.service.AuthService;
import io.vozdarua.controller.service.PasswordRecoveryService;
import io.vozdarua.model.dto.AuthRequest;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.PasswordResetToken;
import io.vozdarua.model.entity.User;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.ratelimit.RateLimited;
import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Objects;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger LOGGER = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService accountService;

    @Inject
    PasswordRecoveryService passwordRecoveryService;

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @POST
    @PermitAll
    @Path("/login")
    @RateLimited(limit = 5, windowSeconds = 60)
    public Response login(AuthRequest request) {

        if (Objects.isNull(request) || Objects.isNull(request.email()) || request.email().isBlank() || Objects.isNull(request.password())
                || request.password().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User user = User.find("email", request.email()).firstResult();
        if (Objects.nonNull(user) && BcryptUtil.matches(request.password(), user.password)) {
            LOGGER.debugf("Login bem-sucedido: %s", VozDaRuaUtils.maskEmail(request.email()));
            return Response.ok(accountService.token(user)).build();
        }

        LOGGER.warnf("Login falhou: %s", VozDaRuaUtils.maskEmail(request.email()));
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @PUT
    @Transactional
    @PermitAll
    @Path("/password/reset")
    @RateLimited(limit = 10, windowSeconds = 3600)
    public Response resetPassword(@QueryParam("token") String tokenValue, AuthRequest authRequest) {
        PasswordResetToken token = PasswordResetToken.find("token", tokenValue).firstResult();

        if (token == null || token.isExpired()) {
            LOGGER.warnf("Reset de senha rejeitado: token inválido ou expirado");
            return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(appMessages.expired_token())).build();
        }

        User user = token.user;
        user.password = BcryptUtil.bcryptHash(authRequest.password());
        user.persist();

        token.delete();

        LOGGER.infof("Senha redefinida: %s", VozDaRuaUtils.maskEmail(user.email));
        return Response.status(Response.Status.CREATED).entity(accountService.token(user)).build();
    }

    @POST
    @PermitAll
    @Path("/password/recovery")
    @RateLimited(limit = 5, windowSeconds = 3600)
    public Response handlePasswordRecovery(AuthRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MessageResponse(appMessages.email_mandatory()))
                    .build();
        }

        return passwordRecoveryService.createRecoveryToken(request.email());

    }
}
