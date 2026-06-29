package io.vozdarua.controller.restclient;

import io.vozdarua.model.dto.GeoResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/search")
@RegisterRestClient(baseUri = "https://nominatim.openstreetmap.org")
public interface GeocodingClient {

    @GET
    List<GeoResponse> getCoordinates(@QueryParam("q") String address, @QueryParam("format") String format, @HeaderParam("User-Agent") String userAgent);
}
