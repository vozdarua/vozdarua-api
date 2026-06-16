package io.fiscalizai.config;

import io.quarkus.qute.i18n.Localized;
import io.fiscalizai.model.messages.AppMessages;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.jboss.logging.Logger;

import java.util.Locale;

@RequestScoped
public class LocaleProducer {

    private static final Logger LOG = Logger.getLogger(LocaleProducer.class);

    @Inject
    @Localized("pt-BR")
    AppMessages appMessagesPtBr;

    @Inject
    @Localized("en")
    AppMessages appMessagesEn;

    @Inject
    HttpHeaders headers;

    @Produces
    @RequestScoped
    @RequestLocale
    public AppMessages getAppMessages() {
        LOG.infof("Getting AppMessages for headers: %s", headers);
        if (headers != null && headers.getAcceptableLanguages() != null && !headers.getAcceptableLanguages().isEmpty()) {
            Locale locale = headers.getAcceptableLanguages().get(0);
            LOG.infof("Detected locale: %s, language: %s", locale, locale.getLanguage());
            if (!locale.equals(Locale.ROOT) && !locale.getLanguage().equals("*")) {
                String lang = locale.getLanguage();
                if ("en".equalsIgnoreCase(lang)) {
                    LOG.info("Returning English messages");
                    return appMessagesEn;
                }
            }
        }
        LOG.info("Returning Portuguese messages");
        return appMessagesPtBr;
    }
}
