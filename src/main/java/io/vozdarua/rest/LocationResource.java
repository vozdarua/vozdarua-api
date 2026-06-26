package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.restclient.BrazilApiClient;
import io.vozdarua.model.dto.CepLocation;
import io.vozdarua.model.dto.ErrorResource;
import io.vozdarua.model.entity.State;
import io.vozdarua.model.messages.AppMessages;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
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
    @PermitAll
    @Path("/cep/{cep}")
    public Response findByCep(@PathParam("cep") String cep) {
        CepLocation dto = brazilClient.findByCep(cep);
        State state = State.find("uf", dto.state()).firstResult();
        return Response.ok(dto.withState(state.name)).build();

    }

    @GET
    @PermitAll
    @Path("/states/")
    public Response states() {
        return Response.ok(State.findAll().list()).build();
    }

}
