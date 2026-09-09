package io.vozdarua.controller.service;

import io.quarkus.qute.Template;
import io.vozdarua.controller.restclient.ResendClient;
import io.vozdarua.controller.restclient.dto.ResendPayload;
import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class);

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
        try {
            resendClient.sendEmail(token, new ResendPayload(from, new String[]{to}, subject, body));
        } catch (Exception e) {
            // Loga com o contexto (destinatário/assunto) e relança - quem chamou decide a
            // resposta HTTP; sem isso a falha desaparecia sem rastro nenhum.
            LOGGER.errorf(e, "Falha ao enviar email '%s' pra %s", subject, VozDaRuaUtils.maskEmail(to));
            throw e;
        }
    }

}
