package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.restclient.GeocodingClient;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.dto.GeoResponse;
import io.vozdarua.model.dto.ImageUploadForm;
import io.vozdarua.model.dto.ContributorRankingDTO;
import io.vozdarua.model.dto.PagedResponse;
import io.vozdarua.model.entity.*;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public Response list(
            @QueryParam("cityId") Long cityId,
            @QueryParam("stateId") Long stateId,
            @QueryParam("neighborhood") String neighborhood,
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("statusId") Long statusId,
            @QueryParam("severityId") Long severityId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        int pageSize = Math.min(Math.max(size, 1), 500); // ponytail: cap simples contra abuso; MapaView usa o teto (500) hoje

        StringBuilder jpql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        appendFilter(jpql, params, "address.cityRef.id", cityId);
        appendFilter(jpql, params, "address.stateRef.id", stateId);
        appendFilter(jpql, params, "address.neighborhood", neighborhood);
        appendFilter(jpql, params, "category.id", categoryId);
        appendFilter(jpql, params, "status.id", statusId);
        appendFilter(jpql, params, "severity.id", severityId);

        PanacheQuery<Issue> query = (jpql.isEmpty() ? Issue.findAll() : Issue.find(jpql.toString(), params.toArray()))
                .page(Math.max(page, 0), pageSize);

        return Response.ok(new PagedResponse<>(query.list(), page, pageSize, query.count(), query.pageCount())).build();
    }

    private static void appendFilter(StringBuilder jpql, List<Object> params, String path, Object value) {
        if (value == null || (value instanceof String s && s.isBlank())) {
            return;
        }
        if (!jpql.isEmpty()) {
            jpql.append(" and ");
        }
        params.add(value);
        jpql.append(path).append(" = ?").append(params.size());
    }

    @PUT
    @PermitAll
    @Transactional
    @Path("{id}/confirm")
    public Response confirmIssue(@PathParam("id") Long id) {
        Issue issue = Issue.findByIdWithCategoryAndTags(id);
        if(Objects.isNull(issue)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
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
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }
        issue.status = Status.find("name", "Resolvido").firstResult();
        issue.confirmResolve = issue.confirmResolve + 1;
        issue.persist();
        return Response.ok(issue).build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }
        return Response.ok(issue).build();
    }

    @POST
    @PermitAll
    @Transactional
    @RateLimited(limit = 10, windowSeconds = 60)
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
                return Response.noContent().entity(new MessageResponse(appMessages.coordenates_not_found())).build();
            }

            issue.address.latitude = Double.parseDouble(geoResponse.get().lat());
            issue.address.longitude = Double.parseDouble(geoResponse.get().lon());
        }

        // Load Severity from database
        if(Objects.nonNull(issue.severity) && Objects.nonNull(issue.severity.id)) {
            Severity severity = Severity.findById(issue.severity.id);
            if(Objects.isNull(severity)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.severity_not_found())).build();
            }
            issue.severity = severity;
        }

        // Load Status from database
        if(Objects.nonNull(issue.status) && Objects.nonNull(issue.status.id)) {
            Status status = Status.findById(issue.status.id);
            if(Objects.isNull(status)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.status_not_found())).build();
            }
            issue.status = status;
        }

        // Load Image from database
        if(Objects.nonNull(issue.photo) && Objects.nonNull(issue.photo.id)) {
            Image photo = Image.findById(issue.photo.id);
            if(Objects.isNull(photo)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse("Image not found")).build();
            }
            issue.photo = photo;
        }

        issue.anonymous = Objects.isNull(issue.reporter) || Objects.isNull(issue.reporter.id);

        if(issue.anonymous) {
            issue.reporter = null;
        }

        resolveCityState(issue.address);

        issue.persist();
        return Response.status(Response.Status.CREATED).entity(issue).build();
    }

    // Best-effort: links the free-text city/state to a real City/State row when the name
    // matches (case-insensitive). Geocoded names don't always match a seeded city exactly
    // (accents, spelling, or a neighborhood instead of the municipality), so a miss here
    // must never block issue creation - it just leaves cityRef/stateRef null.
    private void resolveCityState(Address address) {
        if (Objects.isNull(address) || Objects.isNull(address.city) || address.city.isBlank()) {
            return;
        }
        City match = (Objects.nonNull(address.state) && !address.state.isBlank())
            ? City.find("LOWER(name) = LOWER(?1) and (LOWER(state.name) = LOWER(?2) or LOWER(state.uf) = LOWER(?2))",
                         address.city, address.state).firstResult()
            : City.find("LOWER(name) = LOWER(?1)", address.city).firstResult();
        if (Objects.nonNull(match)) {
            address.cityRef = match;
            address.stateRef = match.state;
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    public Response update(@PathParam("id") Long id, @Valid Issue updatedIssue, @Context SecurityContext securityContext) {
        Issue issue = Issue.findById(id);
        if (issue == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }

        String email = securityContext.getUserPrincipal().getName();
        User reporter = User.<User>find("email", email).singleResultOptional().orElse(null);

        if(Objects.isNull(reporter) || !reporter.email.equals(issue.reporter.email)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new MessageResponse(appMessages.edit_other_user_issue())).build();
        }

        issue.description = VozDaRuaUtils.verifyNull(issue.description, updatedIssue.description);
        issue.confirmIssue = VozDaRuaUtils.verifyNull(issue.confirmIssue, updatedIssue.confirmIssue);
        issue.confirmResolve = VozDaRuaUtils.verifyNull(issue.confirmResolve, updatedIssue.confirmResolve);
        issue.category = VozDaRuaUtils.verifyNull(issue.category, updatedIssue.category);
        issue.photo = VozDaRuaUtils.verifyNull(issue.photo, updatedIssue.photo);
        issue.reporter = VozDaRuaUtils.verifyNull(issue.reporter, updatedIssue.reporter);;
        issue.address = VozDaRuaUtils.verifyNull(issue.address, updatedIssue.address);;

        // Load Severity from database
        if(Objects.nonNull(updatedIssue.severity) && Objects.nonNull(updatedIssue.severity.id)) {
            Severity severity = Severity.findById(updatedIssue.severity.id);
            if(Objects.isNull(severity)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.severity_not_found())).build();
            }
            issue.severity = severity;
        }

        // Load Status from database
        if(Objects.nonNull(updatedIssue.status) && Objects.nonNull(updatedIssue.status.id)) {
            Status status = Status.findById(updatedIssue.status.id);
            if(Objects.isNull(status)) {
                return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.status_not_found())).build();
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
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.issue_not_found())).build();
        }
        Comment.delete("issue.id", id);
        issue.delete();
        return Response.noContent().build();
    }

    @GET
    @RolesAllowed({Roles.ADMIN})
    @Path("/reporter/{reporterId}")
    public Response listByReporter(@PathParam("reporterId") Long reporterId) {
        List<Issue> issues = Issue.list("reporter.id", reporterId);
        if (issues.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MessageResponse(appMessages.no_issues_found_for_reporter())).build();
        }
        return Response.ok(issues).build();
    }

    @POST
    @PermitAll
    @Transactional
    @Path("/image/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RateLimited(limit = 10, windowSeconds = 60)
    public Response uploadToR2(ImageUploadForm form) {

        if (Objects.isNull(form.file) || form.file.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(appMessages.r2_missing_file())).build();
        }

        if (!form.file.contentType().startsWith("image/")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(appMessages.not_valid_image())).build();
        }

        String safeName = VozDaRuaUtils.sanitizeFileName(form.file.fileName());
        String name = UUID.randomUUID() + "-" + safeName;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileKey = folderName + datePath + "/" + name;

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
            return Response.serverError().entity(new MessageResponse(appMessages.r2_upload_file(e.getMessage()))).build();
        }
    }

    @GET
    @PermitAll
    @Path("/ranking")
    public Response ranking(@QueryParam("cityId") Long cityId) {
        List<ContributorRankingDTO> ranking = Issue.rankingByReporter(cityId).stream()
            .map(row -> new ContributorRankingDTO((Long) row[0], (String) row[1],
                VozDaRuaUtils.maskEmail((String) row[2]), (String) row[3], (Long) row[4], (Long) row[5]))
            .toList();
        return Response.ok(ranking).build();
    }

    @GET
    @PermitAll
    @Path("/metrics")
    public Response metrics(@QueryParam("cityId") Long cityId, @QueryParam("neighborhood") String neighborhood) {
        if (cityId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(appMessages.city_id_required())).build();
        }
        return Response.ok(Issue.metricsForCity(cityId, neighborhood)).build();
    }

    private Optional<GeoResponse> getGeoResponse(Address address) {
        List<GeoResponse> responses = geocodingClient.getCoordinates(address.toString(), "json", "MyQuarkusApp/1.0");
        if (responses != null && !responses.isEmpty()) {
            return Optional.of(responses.getFirst());
        }
        return Optional.empty();
    }
}
