package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.restclient.BrazilApiClient;
import io.vozdarua.model.dto.ErrorResource;
import io.vozdarua.model.messages.AppMessages;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    @Inject
    @RestClient
    BrazilApiClient brazilClient;

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @Path("/cep/{cep}")
    public Uni<Response> findByCep(@PathParam("cep") String cep) {
        return brazilClient.findByCep(cep)
                .map(dto -> Response.ok(dto).build())
                .onFailure().recoverWithItem(failure -> {
                    if (failure instanceof WebApplicationException webEx) {
                        int status = webEx.getResponse().getStatus();
                        return Response.status(status)
                                .entity(new ErrorResource(appMessages.cep_not_found(cep)))
                                .build();
                    }
                    return Response.status(Response.Status.BAD_GATEWAY)
                            .entity(new ErrorResource(appMessages.cep_service_unavailable()))
                            .build();
                });

    }

}
