package io.vozdarua.controller.service;

import io.quarkus.qute.Template;
import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.PasswordResetToken;
import io.vozdarua.model.entity.User;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class PasswordRecoveryService {

    private static final Logger LOGGER = Logger.getLogger(PasswordRecoveryService.class);

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @ConfigProperty(name = "app.frontend-url")
    String frontendUrl;

    @Inject
    EmailService emailService;

    @Inject
    Template passwordRecoveryTemplate;

    @Transactional
    public Response createRecoveryToken(String email) {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            // Não revela se o e-mail existe: mesma resposta do caminho feliz, só que
            // sem gerar token nem enviar e-mail de verdade. Sem log aqui de propósito - um log
            // que só dispara nesse branch vira um canal de enumeração de email via log,
            // exatamente o que a resposta idêntica acima já existe pra evitar.
            return Response.ok(new MessageResponse(appMessages.recovery_email_sent())).build();
        }

        // Check if they already requested one recently
        LocalDateTime cooldownLimit = LocalDateTime.now().minusMinutes(1);
        if (user.lastResetRequest != null && user.lastResetRequest.isAfter(cooldownLimit)) {
            LOGGER.warnf("Recuperação de senha em cooldown: %s", VozDaRuaUtils.maskEmail(email));
            return Response.status(Response.Status.BAD_REQUEST).entity(new MessageResponse(appMessages.token_limit_rate())).build();
        }

        // Update the timestamp and proceed
        user.lastResetRequest = LocalDateTime.now();
        user.persist();

        // Clean up any old tokens for this user
        PasswordResetToken.delete("user", user);

        // Generate a secure token
        PasswordResetToken token = new PasswordResetToken();
        token.token = UUID.randomUUID().toString();
        token.user = user;
        token.expiryDate = LocalDateTime.now().plusHours(1); // 1-hour validity
        token.persist();

        // Send Email
        String resetUrl = frontendUrl + "/reset-password?token=" + token.token;
        String subject = appMessages.email_recovery_subject();
        String body = passwordRecoveryTemplate
                .data("subject", subject)
                .data("greeting", appMessages.email_recovery_greeting())
                .data("url", resetUrl)
                .data("buttonLabel", appMessages.email_recovery_button())
                .data("expiryNote", appMessages.email_recovery_expiry())
                .data("ignoreNote", appMessages.email_recovery_ignore())
                .render();
        emailService.send(email, body, subject);
        LOGGER.infof("Email de recuperação de senha enviado: %s", VozDaRuaUtils.maskEmail(email));

        return Response.ok(new MessageResponse(appMessages.recovery_email_sent()))
                .build();
    }
}
