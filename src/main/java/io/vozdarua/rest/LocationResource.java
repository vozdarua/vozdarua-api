package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.controller.restclient.BrazilApiClient;
import io.vozdarua.controller.restclient.GeoapifyClient;
import io.vozdarua.controller.restclient.GeocodingClient;
import io.vozdarua.model.dto.CepLocation;
import io.vozdarua.model.dto.GeoResponse;
import io.vozdarua.model.dto.GeoapifyFeature;
import io.vozdarua.model.dto.GeoapifyPlacesResponse;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.dto.NearbyCityDTO;
import io.vozdarua.model.entity.City;
import io.vozdarua.model.entity.State;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.ratelimit.RateLimited;
import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    private static final Logger LOGGER = Logger.getLogger(LocationResource.class);

    @Inject
    @RestClient
    BrazilApiClient brazilClient;

    @Inject
    @RestClient
    GeocodingClient geocodingClient;

    @Inject
    @RestClient
    GeoapifyClient geoapifyClient;

    @ConfigProperty(name = "geoapify.api-key")
    String geoapifyApiKey;

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
        LOGGER.debugf("Geocodificando endereço=%s", address);

        List<GeoResponse> responses = geocodingClient.getCoordinates(address, "json", "MyQuarkusApp/1.0");

        if (responses != null && !responses.isEmpty()) {
            GeoResponse firstMatch = responses.get(0);

            double latitude = Double.parseDouble(firstMatch.lat());
            double longitude = Double.parseDouble(firstMatch.lon());

            return Response.ok(firstMatch).build();
        } else {
            LOGGER.warnf("Nenhuma coordenada encontrada para endereço=%s", address);
            // 204 (noContent) proíbe corpo na resposta por spec HTTP - a entity() abaixo nunca
            // chegava ao cliente. NOT_FOUND com MessageResponse é o padrão usado no resto da classe.
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse("Nenhuma coordenada encontrada para este endereço."))
                    .build();
        }

    }

    // "populated_place.city,populated_place.town" excludes suburbs/districts/villages that
    // the bare "populated_place" category returns (verified against the real API - a plain
    // "populated_place" filter mostly comes back as neighborhoods of the origin city).
    @GET
    @PermitAll
    @Path("/cities/nearby")
    @RateLimited(limit = 20, windowSeconds = 60)
    public Response nearbyCities(@QueryParam("lat") Double lat, @QueryParam("lng") Double lng) {
        if (lat == null || lng == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new MessageResponse(appMessages.coordinates_required())).build();
        }
        LOGGER.debugf("Buscando cidades próximas de lat=%s lng=%s", lat, lng);

        // Locale.US is mandatory here: quarkus.default-locale=pt_BR would render -23.22 as
        // -23,22, silently corrupting the "circle:lng,lat,radius" filter Geoapify expects.
        String filter = String.format(Locale.US, "circle:%f,%f,50000", lng, lat);
        GeoapifyPlacesResponse response = geoapifyClient.getNearbyPlaces(
            "populated_place.city,populated_place.town", filter, 10, geoapifyApiKey);

        List<NearbyCityDTO> nearby = response.features().stream()
            .map(f -> toNearbyCityDTO(f, lat, lng))
            .sorted(Comparator.comparingLong(NearbyCityDTO::distanceKm))
            .toList();

        // Log do tamanho, não da lista inteira: é o dado que faltava pra saber se o Geoapify
        // devolveu vazio (chave/filtro/cobertura) vs. a chamada nem ter acontecido.
        LOGGER.infof("Cidades próximas encontradas: %d (lat=%s lng=%s)", nearby.size(), lat, lng);

        return Response.ok(nearby).build();
    }

    // Best-effort match against our own seeded City catalog (same name+UF lookup as
    // IssueResource.resolveCityState) so the frontend gets a usable cityId when possible;
    // an unmatched place still renders fine with just name/uf/distance.
    private NearbyCityDTO toNearbyCityDTO(GeoapifyFeature feature, double lat, double lng) {
        var props = feature.properties();
        String name = props.city() != null ? props.city() : props.name();
        String uf = props.stateCode();

        City match = (name != null && uf != null)
            ? City.find("LOWER(name) = LOWER(?1) and state.uf = ?2", name, uf).firstResult()
            : null;

        long distanceKm = Math.round(VozDaRuaUtils.haversineKm(lat, lng, props.lat(), props.lon()));
        return new NearbyCityDTO(match != null ? match.id : null, name, uf, distanceKm);
    }

}
