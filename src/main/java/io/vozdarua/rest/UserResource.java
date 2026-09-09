package io.vozdarua.rest;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.service.AuthService;
import io.vozdarua.controller.service.PasswordRecoveryService;
import io.vozdarua.model.dto.AuthRequest;
import io.vozdarua.model.dto.IssueDTO;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.dto.UserDTO;
import io.vozdarua.model.dto.UserStatsDTO;
import io.vozdarua.model.entity.Comment;
import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.entity.PasswordResetToken;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.ratelimit.RateLimited;
import io.vozdarua.utils.VozDaRuaUtils;
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
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(UserResource.class);

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
        LOGGER.infof("Usuário atualizado: id=%d email=%s", id, VozDaRuaUtils.maskEmail(user.email));
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

    @GET
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    @Path("/me/stats")
    public Response meStats(@QueryParam("cityId") Long cityId, @Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();

        long inCity = cityId != null ? Issue.count("address.cityRef.id = ?1", cityId) : 0;
        long total = Issue.count("reporter.email", email);
        long resolved = Issue.count("reporter.email = ?1 and status.name = ?2", email, "Resolvido");
        // ponytail: "open" via total - resolved instead of "status is null or status.name != X" —
        // that OR silently drops null-status issues, since a dotted path like status.name always
        // compiles to an inner join in JPQL, excluding rows with no status before the OR even runs.
        long open = total - resolved;

        return Response.ok(new UserStatsDTO(inCity, resolved, open)).build();
    }

    @GET
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    @Path("/me/issues")
    public Response myIssues(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        List<IssueDTO> issues = Issue.<Issue>list("reporter.email", email).stream()
                .map(IssueDTO::toIssueDTO)
                .toList();
        return Response.ok(issues).build();
    }

    @DELETE
    @Transactional
    @Path("/me/issues/{id}")
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    public Response deleteMyIssue(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        if (Objects.isNull(issue.reporter) || !issue.reporter.email.equals(email)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new MessageResponse(appMessages.delete_other_user_issue())).build();
        }

        Comment.delete("issue.id", id);
        issue.delete();
        LOGGER.infof("Issue removida pelo autor: id=%d email=%s", id, VozDaRuaUtils.maskEmail(email));
        return Response.noContent().build();
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
        LOGGER.infof("Usuário removido: id=%d email=%s", id, VozDaRuaUtils.maskEmail(user.email));
        return  Response.noContent().build();
    }

}
