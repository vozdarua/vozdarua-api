package io.vozdarua.rest;

import io.vertx.core.http.HttpServerRequest;
import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.Comment;
import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import io.vozdarua.model.messages.AppMessages;
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

import java.util.List;
import java.util.Objects;

@Path("/issues/{issueId}/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @PermitAll
    public Response list(@PathParam("issueId") Long issueId) {
        Issue issue = Issue.findById(issueId);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }
        List<Comment> comments = Comment.list("issue.id = ?1 order by id asc", issueId);
        return Response.ok(comments).build();
    }

    @POST
    @PermitAll
    @Transactional
    public Response create(@PathParam("issueId") Long issueId, @Valid Comment comment,
                            @Context SecurityContext securityContext, @Context HttpServerRequest request) {
        Issue issue = Issue.findById(issueId);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }
        comment.issue = issue;

        if (Objects.nonNull(securityContext.getUserPrincipal())) {
            String email = securityContext.getUserPrincipal().getName();
            User author = User.<User>find("email", email).singleResultOptional().orElse(null);
            comment.author = author;
            comment.authorEmail = Objects.nonNull(author) ? author.email : null;
        }

        comment.ipAddress = VozDaRuaUtils.clientIp(request);
        comment.userAgent = request.getHeader("User-Agent");

        comment.persist();
        return Response.status(Response.Status.CREATED).entity(comment).build();
    }

    @DELETE
    @Path("/{commentId}")
    @Transactional
    @RolesAllowed(Roles.ADMIN)
    public Response delete(@PathParam("issueId") Long issueId, @PathParam("commentId") Long commentId) {
        Comment comment = Comment.findById(commentId);
        if (comment == null || !comment.issue.id.equals(issueId)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.comment_not_found())).build();
        }
        comment.delete();
        return Response.noContent().build();
    }
}
