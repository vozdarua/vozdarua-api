package io.vozdarua.controller.service;

import io.quarkus.qute.Template;
import io.vozdarua.controller.restclient.ResendClient;
import io.vozdarua.controller.restclient.dto.ResendPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class EmailService {

    @ConfigProperty(name = "resend.email.token")
    String token;

    @ConfigProperty(name = "resend.email.from")
    String from;

    @Inject
    Template feedbackTemplate;

    @Inject
    @RestClient
    ResendClient resendClient;

    public void send(String to, String body, String subject) {
        resendClient.sendEmail(token, new ResendPayload(from, new String[]{to}, subject, body));
    }

}
