package io.vozdarua.controller.restclient;

import io.vozdarua.model.dto.GeoapifyPlacesResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v2/places")
@RegisterRestClient(baseUri = "https://api.geoapify.com")
public interface GeoapifyClient {

    @GET
    GeoapifyPlacesResponse getNearbyPlaces(@QueryParam("categories") String categories,
                                            @QueryParam("filter") String filter,
                                            @QueryParam("limit") int limit,
                                            @QueryParam("apiKey") String apiKey);
}
