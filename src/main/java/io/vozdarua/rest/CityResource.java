package io.vozdarua.rest;

import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.CityRankingDTO;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.entity.City;
import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.messages.AppMessages;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/cities")
@Produces(MediaType.APPLICATION_JSON)
public class CityResource {

    @Inject
    @RequestLocale
    AppMessages appMessages;

    @GET
    @PermitAll
    public Response search(@QueryParam("search") String search, @QueryParam("stateId") Long stateId) {
        if (search == null || search.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new MessageResponse(appMessages.city_search_required())).build();
        }

        StringBuilder query = new StringBuilder("LOWER(name) LIKE LOWER(?1)");
        List<Object> params = new ArrayList<>(List.of("%" + search + "%"));
        if (stateId != null) {
            query.append(" and state.id = ?2");
            params.add(stateId);
        }

        List<City> cities = City.find(query.toString(), params.toArray()).page(0, 20).list();
        return Response.ok(cities).build();
    }

    @GET
    @PermitAll
    @Path("/ranking")
    public Response ranking() {
        List<CityRankingDTO> ranking = Issue.rankingByCity().stream()
            .map(r -> new CityRankingDTO((Long) r[0], (String) r[1], (String) r[2], (Long) r[3], (Long) r[4]))
            .toList();
        return Response.ok(ranking).build();
    }
}
