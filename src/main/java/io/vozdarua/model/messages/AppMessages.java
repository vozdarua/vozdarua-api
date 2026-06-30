package io.vozdarua.model.messages;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

@MessageBundle(value = "AppMessages")
public interface AppMessages {

    @Message
    String categories_not_found();

    @Message
    String issue_not_found();

    @Message
    String severities_not_found();

    @Message
    String statuses_not_found();

    @Message
    String cep_not_found(String cep);

    @Message
    String cep_service_unavailable();

    @Message
    String user_required();

    @Message
    String user_not_found();

    @Message
    String severity_not_found();

    @Message
    String status_not_found();

    @Message
    String no_issues_found_for_category();

    @Message
    String no_issues_found_for_status();

    @Message
    String no_issues_found_for_severity();

    @Message
    String no_issues_found_for_reporter();

    @Message
    String no_issues_found_for_city(String cityName);

    @Message
    String no_issues_found_for_address();

    @Message
    String address_parameters_required();

    @Message
    String r2_missing_file();

    @Message
    String r2_upload_file(String exception);

    @Message
    String not_valid_image();

    @Message
    String edit_other_user_issue();

    @Message
    String coordenates_not_found();

    @Message
    String user_exists();
}
