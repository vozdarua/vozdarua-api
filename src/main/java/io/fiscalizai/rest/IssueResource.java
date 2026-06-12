package io.fiscalizai.rest;

import io.fiscalizai.model.entity.Issue;
import io.fiscalizai.model.entity.Severity;
import io.fiscalizai.model.entity.Status;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/issues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueResource {

    @GET
    public List<Issue> listAll() {
        return Issue.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(issue).build();
    }

    @POST
    @Transactional
    public Response create(@Valid Issue issue) {
        issue.persist();
        return Response.status(Response.Status.CREATED).entity(issue).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, @Valid Issue updatedIssue) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        issue.description = updatedIssue.description;
        issue.severity = updatedIssue.severity;
        issue.status = updatedIssue.status;
        issue.confirmIssue = updatedIssue.confirmIssue;
        issue.category = updatedIssue.category;
        issue.photo = updatedIssue.photo;
        issue.reporter = updatedIssue.reporter;
        issue.address = updatedIssue.address;

        return Response.ok(issue).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        issue.delete();
        return Response.noContent().build();
    }

    @GET
    @Path("/category/{categoryId}")
    public List<Issue> listByCategory(@PathParam("categoryId") Long categoryId) {
        return Issue.list("category.id", categoryId);
    }

    @GET
    @Path("/status/{status}")
    public List<Issue> listByStatus(@PathParam("status") Status status) {
        return Issue.list("status", status);
    }

    @GET
    @Path("/severity/{severity}")
    public List<Issue> listBySeverity(@PathParam("severity") Severity severity) {
        return Issue.list("severity", severity);
    }

    @GET
    @Path("/reporter/{reporterId}")
    public List<Issue> listByReporter(@PathParam("reporterId") Long reporterId) {
        return Issue.list("reporter.id", reporterId);
    }
}
