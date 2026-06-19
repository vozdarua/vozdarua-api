package io.fiscalizai.rest;

import io.fiscalizai.config.RequestLocale;
import io.fiscalizai.model.dto.ErrorResource;
import io.fiscalizai.model.dto.ImageUploadForm;
import io.fiscalizai.model.entity.*;
import io.fiscalizai.model.messages.AppMessages;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Path("/issues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @Inject
    S3Client s3;

    @ConfigProperty(name = "cloudflare.r2.bucket-name")
    String bucketName;

    @ConfigProperty(name = "cloudflare.r2.public-url")
    String publicR2Url;

    @ConfigProperty(name= "cloudflare.r2.folder-name")
    private String folderName;

    @GET
    public Response listAll() {
        List<Issue> issues = Issue.listAll();
        if(issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        return Response.ok(Issue.listAll()).build();
    }

    @PUT
    @Transactional
    @Path("confirm/{id}")
    public Response confirmIssue(@PathParam("id") Long id) {
        Issue issue = Issue.findByIdWithCategoryAndTags(id);
        if(Objects.isNull(issue)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }

        issue.confirmIssue = issue.confirmIssue + 1;
        issue.persist();

        return Response.ok(issue).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        return Response.ok(issue).build();
    }

    @POST
    @Transactional
    public Response create(@Valid Issue issue) {

        if(Objects.isNull(issue.reporter.id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.user_required())).build();
        }

        FiscalizaiUser reporter = FiscalizaiUser.findById(issue.reporter.id);

        if(Objects.isNull(reporter)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.user_not_found())).build();
        }

        // Load Severity from database
        if(Objects.nonNull(issue.severity) && Objects.nonNull(issue.severity.id)) {
            Severity severity = Severity.findById(issue.severity.id);
            if(Objects.isNull(severity)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.severity_not_found())).build();
            }
            issue.severity = severity;
        }

        // Load Status from database
        if(Objects.nonNull(issue.status) && Objects.nonNull(issue.status.id)) {
            Status status = Status.findById(issue.status.id);
            if(Objects.isNull(status)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.status_not_found())).build();
            }
            issue.status = status;
        }

        issue.reporter = reporter;
        issue.persist();
        return Response.status(Response.Status.CREATED).entity(issue).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, @Valid Issue updatedIssue) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }

        issue.description = updatedIssue.description;
        issue.confirmIssue = updatedIssue.confirmIssue;
        issue.category = updatedIssue.category;
        issue.photo = updatedIssue.photo;
        issue.reporter = updatedIssue.reporter;
        issue.address = updatedIssue.address;

        // Load Severity from database
        if(Objects.nonNull(updatedIssue.severity) && Objects.nonNull(updatedIssue.severity.id)) {
            Severity severity = Severity.findById(updatedIssue.severity.id);
            if(Objects.isNull(severity)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.severity_not_found())).build();
            }
            issue.severity = severity;
        }

        // Load Status from database
        if(Objects.nonNull(updatedIssue.status) && Objects.nonNull(updatedIssue.status.id)) {
            Status status = Status.findById(updatedIssue.status.id);
            if(Objects.isNull(status)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.status_not_found())).build();
            }
            issue.status = status;
        }

        return Response.ok(issue).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        issue.delete();
        return Response.noContent().build();
    }

    @GET
    @Path("/category/{categoryId}")
    public Response listByCategory(@PathParam("categoryId") Long categoryId) {
        List<Issue> issues = Issue.list("category.id", categoryId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_category())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @Path("/status/{statusId}")
    public Response listByStatus(@PathParam("statusId") Long statusId) {
        List<Issue> issues = Issue.list("status.id", statusId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_status())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @Path("/severity/{severityId}")
    public Response listBySeverity(@PathParam("severityId") Long severityId) {
        List<Issue> issues = Issue.list("severity.id", severityId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_severity())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @Path("/reporter/{reporterId}")
    public Response listByReporter(@PathParam("reporterId") Long reporterId) {
        List<Issue> issues = Issue.list("reporter.id", reporterId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_reporter())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @Path("/address")
    public Response listByAddress(
            @QueryParam("city") String cityName,
            @QueryParam("state") String state,
            @QueryParam("neighborhood") String neighborhood) {

        StringBuilder queryBuilder = new StringBuilder();
        Object[] params = new Object[3];
        int paramIndex = 0;

        if (cityName != null && !cityName.isEmpty()) {
            queryBuilder.append("address.city = ?").append(++paramIndex);
            params[paramIndex - 1] = cityName;
        }

        if (state != null && !state.isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" and ");
            }
            queryBuilder.append("address.state = ?").append(++paramIndex);
            params[paramIndex - 1] = state;
        }

        if (neighborhood != null && !neighborhood.isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" and ");
            }
            queryBuilder.append("address.neighborhood = ?").append(++paramIndex);
            params[paramIndex - 1] = neighborhood;
        }

        if (queryBuilder.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResource(appMessages.address_parameters_required()))
                    .build();
        }

        Object[] actualParams = new Object[paramIndex];
        System.arraycopy(params, 0, actualParams, 0, paramIndex);

        List<Issue> issues = Issue.list(queryBuilder.toString(), actualParams);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResource(appMessages.no_issues_found_for_address()))
                    .build();
        }
        return Response.ok(issues).build();
    }

    @POST
    @Path("/image/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response uploadToR2(ImageUploadForm form) {

        if (Objects.isNull(form.file) || form.file.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResource(appMessages.r2_missing_file())).build();
        }

        if (!form.file.contentType().startsWith("image/")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResource(appMessages.not_valid_image())).build();
        }

        String name = UUID.randomUUID() + "-" + form.file.fileName();
        String fileKey = folderName + name;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(form.file.contentType())
                    .build();

            // Stream the file up to Cloudflare R2
            s3.putObject(putRequest, RequestBody.fromFile(form.file.filePath()));

            // Construct the access URL if public access is enabled in Cloudflare dashboard
            String publicUrl = publicR2Url + fileKey;

            Image image = new Image();
            image.name = name;
            image.s3Key = fileKey;
            image.s3Url = publicUrl;
            image.persist();

            return Response.ok(image).build();

        } catch (Exception e) {
            return Response.serverError().entity(new ErrorResource(appMessages.r2_upload_file(e.getMessage()))).build();
        }
    }
}
