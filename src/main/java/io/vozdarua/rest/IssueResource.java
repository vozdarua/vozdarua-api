package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.restclient.GeocodingClient;
import io.vozdarua.model.dto.ErrorResource;
import io.vozdarua.model.dto.GeoResponse;
import io.vozdarua.model.dto.ImageUploadForm;
import io.vozdarua.model.entity.*;
import io.vozdarua.model.messages.AppMessages;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    String folderName;

    @Inject
    @RestClient
    GeocodingClient geocodingClient;

    @GET
    @PermitAll
    public Response list() {
        List<Issue> issues = Issue.listAll();
        if(issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        return Response.ok(Issue.listAll()).build();
    }

    @PUT
    @PermitAll
    @Transactional
    @Path("{id}/confirm")
    public Response confirmIssue(@PathParam("id") Long id) {
        Issue issue = Issue.findByIdWithCategoryAndTags(id);
        if(Objects.isNull(issue)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }

        issue.confirmIssue = issue.confirmIssue + 1;
        issue.persist();

        return Response.ok(issue).build();
    }

    @PUT
    @PermitAll
    @Transactional
    @Path("{id}/resolve")
    public Response resolveIssue(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if(Objects.isNull(issue)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        issue.status = Status.find("name", "Resolvido").firstResult();
        issue.persist();
        return Response.ok(issue).build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        return Response.ok(issue).build();
    }

    @POST
    @PermitAll
    @Transactional
    public Response create(@Valid Issue issue, @Context SecurityContext securityContext) {
        if(Objects.nonNull(securityContext.getUserPrincipal())) {
            String email = securityContext.getUserPrincipal().getName();
            User reporter = User.<User>find("email", email).singleResultOptional().orElse(null);

            if(Objects.nonNull(reporter)) {
                issue.reporter = reporter;
            }

            if(Objects.nonNull(issue.reporter) && !issue.anonymous) {
                issue.reporter.persist();
            }
        }

        if(Objects.isNull(issue.address.latitude) || Objects.isNull(issue.address.longitude)) {
            Optional<GeoResponse> geoResponse = getGeoResponse(issue.address);
            if(geoResponse.isEmpty()) {
                return Response.noContent().entity(new ErrorResource(appMessages.coordenates_not_found())).build();
            }

            issue.address.latitude = Double.parseDouble(geoResponse.get().lat());
            issue.address.longitude = Double.parseDouble(geoResponse.get().lon());
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

        // Load Image from database
        if(Objects.nonNull(issue.photo) && Objects.nonNull(issue.photo.id)) {
            Image photo = Image.findById(issue.photo.id);
            if(Objects.isNull(photo)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource("Image not found")).build();
            }
            issue.photo = photo;
        }

        issue.anonymous = Objects.isNull(issue.reporter) || Objects.isNull(issue.reporter.id);

        if(issue.anonymous) {
            issue.reporter = null;
        }

        issue.persist();
        return Response.status(Response.Status.CREATED).entity(issue).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    public Response update(@PathParam("id") Long id, @Valid Issue updatedIssue, @Context SecurityContext securityContext) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        User reporter = User.<User>find("email", email).singleResultOptional().orElse(null);

        if(Objects.isNull(reporter) || !reporter.email.equals(issue.reporter.email)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResource(appMessages.edit_other_user_issue())).build();
        }

        issue.description = VozDaRuaUtils.verifyNull(issue.description, updatedIssue.description);
        issue.confirmIssue = VozDaRuaUtils.verifyNull(issue.confirmIssue, updatedIssue.confirmIssue);
        issue.category = VozDaRuaUtils.verifyNull(issue.category, updatedIssue.category);
        issue.photo = VozDaRuaUtils.verifyNull(issue.photo, updatedIssue.photo);
        issue.reporter = VozDaRuaUtils.verifyNull(issue.reporter, updatedIssue.reporter);;
        issue.address = VozDaRuaUtils.verifyNull(issue.address, updatedIssue.address);;

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
    @RolesAllowed({Roles.ADMIN})
    public Response delete(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.issue_not_found())).build();
        }
        issue.delete();
        return Response.noContent().build();
    }

    @GET
    @PermitAll
    @Path("/category/{categoryId}")
    public Response listByCategory(@PathParam("categoryId") Long categoryId) {
        List<Issue> issues = Issue.list("category.id", categoryId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_category())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @PermitAll
    @Path("/status/{statusId}")
    public Response listByStatus(@PathParam("statusId") Long statusId) {
        List<Issue> issues = Issue.list("status.id", statusId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_status())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @PermitAll
    @Path("/severity/{severityId}")
    public Response listBySeverity(@PathParam("severityId") Long severityId) {
        List<Issue> issues = Issue.list("severity.id", severityId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_severity())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @RolesAllowed({Roles.ADMIN})
    @Path("/reporter/{reporterId}")
    public Response listByReporter(@PathParam("reporterId") Long reporterId) {
        List<Issue> issues = Issue.list("reporter.id", reporterId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResource(appMessages.no_issues_found_for_reporter())).build();
        }
        return Response.ok(issues).build();
    }

    @GET
    @PermitAll
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
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append(" and ");
            }
            queryBuilder.append("address.state = ?").append(++paramIndex);
            params[paramIndex - 1] = state;
        }

        if (neighborhood != null && !neighborhood.isEmpty()) {
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append(" and ");
            }
            queryBuilder.append("address.neighborhood = ?").append(++paramIndex);
            params[paramIndex - 1] = neighborhood;
        }

        if (queryBuilder.isEmpty()) {
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
    @PermitAll
    @Transactional
    @Path("/image/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
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

    private Optional<GeoResponse> getGeoResponse(Address address) {
        List<GeoResponse> responses = geocodingClient.getCoordinates(address.toString(), "json", "MyQuarkusApp/1.0");
        if (responses != null && !responses.isEmpty()) {
            return Optional.of(responses.getFirst());
        }
        return Optional.empty();
    }
}
