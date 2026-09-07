package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.restclient.BrazilApiClient;
import io.vozdarua.controller.restclient.GeocodingClient;
import io.vozdarua.model.dto.CepLocation;
import io.vozdarua.model.dto.GeoResponse;
import io.vozdarua.model.entity.State;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.ratelimit.RateLimited;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;

import java.util.List;

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    @Inject
    @RestClient
    BrazilApiClient brazilClient;

    @Inject
    @RestClient
    GeocodingClient geocodingClient;

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @PermitAll
    @Path("/cep/{cep}")
    @RateLimited(limit = 20, windowSeconds = 60)
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

    @GET
    @Path("/coordenate/")
    @RateLimited(limit = 20, windowSeconds = 60)
    public Response getLatLongfromAddress(@QueryParam("address") String address) {

        List<GeoResponse> responses = geocodingClient.getCoordinates(address, "json", "MyQuarkusApp/1.0");

        if (responses != null && !responses.isEmpty()) {
            GeoResponse firstMatch = responses.get(0);

            double latitude = Double.parseDouble(firstMatch.lat());
            double longitude = Double.parseDouble(firstMatch.lon());

            return Response.ok(firstMatch).build();
        } else {
            return Response.noContent().entity("No coordinates found for this address.").build();
        }

    }


}
