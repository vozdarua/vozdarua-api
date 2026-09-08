package io.vozdarua.controller.service;

import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Renders the Qute email templates directly (no ResendClient/network involved) - the
// real risk here is a typo in a {placeholder} name that Qute would silently leave
// unresolved or throw on, which no other test would catch since nothing exercises
// FeedbackResource/PasswordRecoveryService's actual email sending today.
@QuarkusTest
class EmailTemplatesTest {

    @Inject
    Template feedbackTemplate;

    @Inject
    Template passwordRecoveryTemplate;

    @Test
    void feedbackTemplateRendersAllFields() {
        String html = feedbackTemplate
                .data("name", "Maria Souza")
                .data("email", "maria@example.com")
                .data("type", "Sugestão")
                .data("message", "Adorei o app!")
                .render();

        assertTrue(html.contains("Maria Souza"));
        assertTrue(html.contains("maria@example.com"));
        assertTrue(html.contains("Sugestão"));
        assertTrue(html.contains("Adorei o app!"));
    }

    @Test
    void passwordRecoveryTemplateRendersAllFields() {
        String html = passwordRecoveryTemplate
                .data("subject", "Solicitação de redefinição de senha")
                .data("greeting", "Recebemos uma solicitação para redefinir sua senha.")
                .data("url", "https://vozdarua.com.br/reset-password?token=abc123")
                .data("buttonLabel", "Redefinir senha")
                .data("expiryNote", "Este link expira em 1 hora.")
                .data("ignoreNote", "Se você não solicitou isso, ignore este e-mail.")
                .render();

        assertTrue(html.contains("Solicitação de redefinição de senha"));
        assertTrue(html.contains("https://vozdarua.com.br/reset-password?token=abc123"));
        assertTrue(html.contains("Redefinir senha"));
        assertTrue(html.contains("Este link expira em 1 hora."));
        assertTrue(html.contains("Se você não solicitou isso, ignore este e-mail."));
    }
}
