package io.vozdarua.rest;

import io.quarkus.qute.Template;
import io.vozdarua.controller.restclient.ResendClient;
import io.vozdarua.controller.restclient.dto.ResendPayload;
import io.vozdarua.controller.service.EmailService;
import io.vozdarua.model.entity.Feedback;
import io.vozdarua.ratelimit.RateLimited;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @ConfigProperty(name = "resend.email.vozdarua")
    String toVozDaRua;

    @Inject
    Template feedbackTemplate;

    @Inject
    EmailService emailService;

    @POST
    @PermitAll
    @Transactional
    @RateLimited(limit = 5, windowSeconds = 3600)
    public Response create(@Valid Feedback feedback) {
        sendEmail(feedback);
        feedback.persist();
        return Response.status(Response.Status.CREATED).build();
    }


    public void sendEmail(Feedback feedback) {
        var body = feedbackTemplate.data("name", feedback.name)
                .data("email", feedback.email)
                .data("message", feedback.message)
                .data("type", feedback.type)
                .render();
        emailService.send(toVozDaRua, body, "Novo Feedback: Voz da Rua App");
        //TODO: retornar retorno e salvar no banco?
    }
}
