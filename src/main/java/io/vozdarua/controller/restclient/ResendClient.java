package io.vozdarua.controller.restclient;

import io.vozdarua.controller.restclient.dto.ResendPayload;
import io.vozdarua.controller.restclient.dto.ResendResponse;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.resend.com")
public interface ResendClient {

    @POST
    @Path("/emails")
    ResendResponse sendEmail(
            @HeaderParam("Authorization") String authorization,
            ResendPayload payload
    );
}
