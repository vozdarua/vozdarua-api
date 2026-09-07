package io.vozdarua.controller.service;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.PasswordResetToken;
import io.vozdarua.model.entity.User;
import io.vozdarua.model.messages.AppMessages;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class PasswordRecoveryService {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @ConfigProperty(name = "app.frontend-url")
    String frontendUrl;

    @Inject
    EmailService emailService;

    @Transactional
    public Response createRecoveryToken(String email) {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            // Não revela se o e-mail existe: mesma resposta do caminho feliz, só que
            // sem gerar token nem enviar e-mail de verdade.
            return Response.ok(new MessageResponse(appMessages.recovery_email_sent())).build();
        }

        // Check if they already requested one recently
        LocalDateTime cooldownLimit = LocalDateTime.now().minusMinutes(1);
        if (user.lastResetRequest != null && user.lastResetRequest.isAfter(cooldownLimit)) {
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
        emailService.send(email, appMessages.email_recovery_text(resetUrl), appMessages.email_recovery_subject());

        return Response.ok(new MessageResponse(appMessages.recovery_email_sent()))
                .build();
    }
}
